package capstone.ui;

import capstone.model.Order;
import capstone.model.Product;
import capstone.model.User;
import capstone.service.AuthService;
import capstone.service.OrderService;
import capstone.service.ProductService;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BuyerMenu {
    private final Scanner sc;
    private final AuthService authService;
    private final ProductService productService;
    private final OrderService orderService;

    public BuyerMenu(Scanner sc, AuthService authService, ProductService productService, OrderService orderService) {
        this.sc = sc;
        this.authService = authService;
        this.productService = productService;
        this.orderService = orderService;
    }

    public void run() {
        boolean stay = true;
        while (stay) {
            User me = authService.getCurrentUser();
            System.out.println("\n=== BUYER DASHBOARD (" + me.getUsername() + ") ===");
            System.out.println("1. Browse all products");
            System.out.println("2. Search products");
            System.out.println("3. Place an order");
            System.out.println("4. View my orders");
            System.out.println("5. Cancel an order");
            System.out.println("6. Logout");
            System.out.print("Choose: ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": browseProducts(); break;
                case "2": searchProducts(); break;
                case "3": placeOrder(); break;
                case "4": viewMyOrders(); break;
                case "5": cancelOrder(); break;
                case "6":
                    authService.logout();
                    System.out.println("Logged out.");
                    stay = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void browseProducts() {
        List<Product> all = productService.listAll();
        if (all.isEmpty()) {
            System.out.println("No products available right now.");
            return;
        }
        for (int i = 0; i < all.size(); i++) {
            System.out.println(all.get(i));
        }
    }

    private void searchProducts() {
        System.out.print("Keyword: ");
        String kw = sc.nextLine().trim();
        List<Product> results = productService.search(kw);
        if (results.isEmpty()) {
            System.out.println("No matches for '" + kw + "'.");
            return;
        }
        for (int i = 0; i < results.size(); i++) {
            System.out.println(results.get(i));
        }
    }

    private void placeOrder() {
        List<String[]> cart = new ArrayList<>();
        boolean addingMore = true;

        while (addingMore) {
            System.out.print("Product ID (or 'done' to finish): ");
            String pid = sc.nextLine().trim();
            if (pid.equalsIgnoreCase("done")) {
                addingMore = false;
                continue;
            }
            System.out.print("Quantity: ");
            String qtyRaw = sc.nextLine().trim();
            int qty;
            try {
                qty = Integer.parseInt(qtyRaw);
            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity, line skipped.");
                continue;
            }
            cart.add(new String[]{pid, String.valueOf(qty)});
            System.out.println("Added to cart. Add another item or type 'done'.");
        }

        if (cart.isEmpty()) {
            System.out.println("Cart empty, order cancelled.");
            return;
        }

        try {
            Order order = orderService.placeOrder(authService.getCurrentUser(), cart);
            System.out.println("Order placed!\n" + order);
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewMyOrders() {
        List<Order> mine = orderService.listByBuyer(authService.getCurrentUser().getUserId());
        if (mine.isEmpty()) {
            System.out.println("You haven't placed any orders yet.");
            return;
        }
        for (int i = 0; i < mine.size(); i++) {
            System.out.println(mine.get(i));
        }
    }

    private void cancelOrder() {
        System.out.print("Order ID to cancel: ");
        String oid = sc.nextLine().trim();
        try {
            orderService.cancelOrder(authService.getCurrentUser(), oid);
            System.out.println("Order cancelled and stock restored.");
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
