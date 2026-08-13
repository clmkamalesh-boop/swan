package capstone.service;

import capstone.exception.InvalidInputException;
import capstone.exception.OrderNotFoundException;
import capstone.exception.StockDepletedException;
import capstone.exception.UnauthorizedAccessException;
import capstone.model.Order;
import capstone.model.OrderItem;
import capstone.model.OrderStatus;
import capstone.model.Product;
import capstone.model.Role;
import capstone.model.Seller;
import capstone.model.User;
import capstone.repository.OrderRepository;
import capstone.repository.ProductRepository;
import capstone.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;

public class OrderService {
    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;

    public OrderService(OrderRepository orderRepo, ProductRepository productRepo, UserRepository userRepo) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
        this.userRepo = userRepo;
    }

    // cart is a simple list of productId/qty pairs built by the UI layer
    public Order placeOrder(User actor, List<String[]> cartLines) {
        if (actor.getRole() != Role.BUYER) {
            throw new UnauthorizedAccessException("Only buyers can place orders.");
        }
        if (cartLines == null || cartLines.isEmpty()) {
            throw new InvalidInputException("Cart is empty.");
        }

        List<OrderItem> items = new ArrayList<>();

        // first pass: validate everything before we touch stock, so a bad line doesn't
        // partially decrement inventory on a failed order
        for (int i = 0; i < cartLines.size(); i++) {
            String pid = cartLines.get(i)[0];
            int qty = Integer.parseInt(cartLines.get(i)[1]);

            Product p = productRepo.findById(pid);
            if (p == null) {
                throw new InvalidInputException("Product " + pid + " does not exist.");
            }
            if (qty <= 0) {
                throw new InvalidInputException("Quantity for " + p.getName() + " must be positive.");
            }
            if (p.getStock() < qty) {
                throw new StockDepletedException("Not enough stock for " + p.getName() +
                        " (requested " + qty + ", available " + p.getStock() + ").");
            }
        }

        // second pass: commit
        for (int i = 0; i < cartLines.size(); i++) {
            String pid = cartLines.get(i)[0];
            int qty = Integer.parseInt(cartLines.get(i)[1]);
            Product p = productRepo.findById(pid);

            p.decreaseStock(qty);
            items.add(new OrderItem(p.getProductId(), p.getName(), p.getSellerId(), qty, p.getPrice()));
        }

        String orderId = orderRepo.generateOrderId();
        Order order = new Order(orderId, actor.getUserId(), items);
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepo.save(order);

        // credit each seller involved with their share of the sale
        for (int i = 0; i < items.size(); i++) {
            OrderItem it = items.get(i);
            User sellerUser = userRepo.findById(it.getSellerId());
            if (sellerUser instanceof Seller) {
                ((Seller) sellerUser).addSale(it.getLineTotal());
            }
        }

        return order;
    }

    public void cancelOrder(User actor, String orderId) {
        Order o = orderRepo.findById(orderId);
        if (o == null) {
            throw new OrderNotFoundException("No order with id " + orderId);
        }
        boolean isOwner = actor.getRole() == Role.BUYER && o.getBuyerId().equals(actor.getUserId());
        boolean isAdmin = actor.getRole() == Role.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new UnauthorizedAccessException("You cannot cancel this order.");
        }
        if (o.getStatus() == OrderStatus.CANCELLED || o.getStatus() == OrderStatus.DELIVERED) {
            throw new InvalidInputException("Order cannot be cancelled from status " + o.getStatus() + ".");
        }

        // restock everything from the cancelled order
        List<OrderItem> items = o.getItems();
        for (int i = 0; i < items.size(); i++) {
            Product p = productRepo.findById(items.get(i).getProductId());
            if (p != null) {
                p.increaseStock(items.get(i).getQuantity());
            }
        }
        o.setStatus(OrderStatus.CANCELLED);
    }

    public void updateStatus(User actor, String orderId, OrderStatus newStatus) {
        Order o = orderRepo.findById(orderId);
        if (o == null) {
            throw new OrderNotFoundException("No order with id " + orderId);
        }

        boolean isSellerOfSomeItem = false;
        if (actor.getRole() == Role.SELLER) {
            List<OrderItem> items = o.getItems();
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getSellerId().equals(actor.getUserId())) {
                    isSellerOfSomeItem = true;
                    break;
                }
            }
        }
        if (actor.getRole() != Role.ADMIN && !isSellerOfSomeItem) {
            throw new UnauthorizedAccessException("You are not associated with this order.");
        }
        if (o.getStatus() == OrderStatus.CANCELLED || o.getStatus() == OrderStatus.DELIVERED) {
            throw new InvalidInputException("Order in status " + o.getStatus() + " cannot be updated further.");
        }
        o.setStatus(newStatus);
    }

    public List<Order> listByBuyer(String buyerId) {
        List<Order> out = new ArrayList<>();
        for (Order o : orderRepo.getAll().values()) {
            if (o.getBuyerId().equals(buyerId)) {
                out.add(o);
            }
        }
        return out;
    }

    public List<Order> listBySeller(String sellerId) {
        List<Order> out = new ArrayList<>();
        for (Order o : orderRepo.getAll().values()) {
            List<OrderItem> items = o.getItems();
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getSellerId().equals(sellerId)) {
                    out.add(o);
                    break;
                }
            }
        }
        return out;
    }

    public List<Order> listAll() {
        return new ArrayList<>(orderRepo.getAll().values());
    }
}
