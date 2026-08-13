package capstone.ui;

import capstone.model.Order;
import capstone.model.Product;
import capstone.model.Seller;
import capstone.model.User;
import capstone.service.AuthService;
import capstone.service.OrderService;
import capstone.service.ProductService;
import java.util.List;
import java.util.Scanner;

public class SellerMenu {
    private final Scanner sc;
    private final AuthService authService;
    private final ProductService productService;
    private final OrderService orderService;

    public SellerMenu(Scanner sc, AuthService authService, ProductService productService, OrderService orderService) {
        this.sc = sc;
        this.authService = authService;
        this.productService = productService;
        this.orderService = orderService;
    }

    public void run() {
        boolean stay = true;
        while (stay) {
            User me = authService.getCurrentUser();
            Seller seller = (Seller) me;
            System.out.println("\n=== SELLER DASHBOARD (" + seller.getStoreName() + ") ===");
            System.out.println("Total sales: $" + String.format("%.2f", seller.getTotalSalesValue()));
            System.out.println("1. Add product");
            System.out.println("2. Update my product");
            System.out.println("3. Delete my product");
            System.out.println("4. View my products");
            System.out.println("5. View orders for my products");
            System.out.println("6. Update order status (for my items)");
            System.out.println("7. Logout");
            System.out.print("Choose: ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": addProduct(); break;
                case "2": updateProduct(); break;
                case "3": deleteProduct(); break;
                case "4": viewMyProducts(); break;
                case "5": viewMyOrders(); break;
                case "6": updateOrderStatus(); break;
                case "7":
                    authService.logout();
                    System.out.println("Logged out.");
                    stay = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = sc.nextLine().trim();
            try {
                return Double.parseDouble(raw);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = sc.nextLine().trim();
            try {
                return Integer.parseInt(raw);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private void addProduct() {
        System.out.print("Product name: ");
        String name = sc.nextLine().trim();
        System.out.print("Description: ");
        String desc = sc.nextLine().trim();
        System.out.print("Category: ");
        String cat = sc.nextLine().trim();
        double price = readDouble("Price: ");
        int stock = readInt("Stock quantity: ");

        try {
            Product p = productService.addProduct(authService.getCurrentUser(), name, desc, cat, price, stock);
            System.out.println("Added: " + p);
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void updateProduct() {
        System.out.print("Product ID to update: ");
        String pid = sc.nextLine().trim();
        System.out.print("New name (blank to keep): ");
        String name = sc.nextLine().trim();
        System.out.print("New description (blank to keep): ");
        String desc = sc.nextLine().trim();
        System.out.print("New category (blank to keep): ");
        String cat = sc.nextLine().trim();
        System.out.print("New price (blank to keep): ");
        String priceRaw = sc.nextLine().trim();
        System.out.print("New stock (blank to keep): ");
        String stockRaw = sc.nextLine().trim();

        Double price = null;
        Integer stock = null;
        try {
            if (!priceRaw.isEmpty()) price = Double.parseDouble(priceRaw);
            if (!stockRaw.isEmpty()) stock = Integer.parseInt(stockRaw);
        } catch (NumberFormatException e) {
            System.out.println("Invalid numeric input, update cancelled.");
            return;
        }

        try {
            productService.updateProduct(authService.getCurrentUser(), pid,
                    name.isEmpty() ? null : name,
                    desc.isEmpty() ? null : desc,
                    cat.isEmpty() ? null : cat,
                    price, stock);
            System.out.println("Product updated.");
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void deleteProduct() {
        System.out.print("Product ID to delete: ");
        String pid = sc.nextLine().trim();
        try {
            productService.deleteProduct(authService.getCurrentUser(), pid);
            System.out.println("Product deleted.");
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewMyProducts() {
        List<Product> mine = productService.listBySeller(authService.getCurrentUser().getUserId());
        if (mine.isEmpty()) {
            System.out.println("You have no products listed.");
            return;
        }
        for (int i = 0; i < mine.size(); i++) {
            System.out.println(mine.get(i));
        }
    }

    private void viewMyOrders() {
        List<Order> mine = orderService.listBySeller(authService.getCurrentUser().getUserId());
        if (mine.isEmpty()) {
            System.out.println("No orders involve your products yet.");
            return;
        }
        for (int i = 0; i < mine.size(); i++) {
            System.out.println(mine.get(i));
        }
    }

    private void updateOrderStatus() {
        System.out.print("Order ID: ");
        String oid = sc.nextLine().trim();
        System.out.print("New status (CONFIRMED/SHIPPED/DELIVERED): ");
        String statusRaw = sc.nextLine().trim().toUpperCase();
        try {
            capstone.model.OrderStatus status = capstone.model.OrderStatus.valueOf(statusRaw);
            orderService.updateStatus(authService.getCurrentUser(), oid, status);
            System.out.println("Order status updated.");
        } catch (IllegalArgumentException e) {
            System.out.println("Unknown status value.");
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
