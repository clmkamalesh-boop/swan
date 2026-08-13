package capstone.ui;

import capstone.model.Order;
import capstone.model.Product;
import capstone.model.User;
import capstone.service.AuthService;
import capstone.service.OrderService;
import capstone.service.ProductService;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private final Scanner sc;
    private final AuthService authService;
    private final ProductService productService;
    private final OrderService orderService;

    public AdminMenu(Scanner sc, AuthService authService, ProductService productService, OrderService orderService) {
        this.sc = sc;
        this.authService = authService;
        this.productService = productService;
        this.orderService = orderService;
    }

    public void run() {
        boolean stay = true;
        while (stay) {
            User me = authService.getCurrentUser();
            System.out.println("\n=== ADMIN DASHBOARD (" + me.getUsername() + ") ===");
            System.out.println("1. View all users");
            System.out.println("2. View all products");
            System.out.println("3. Delete any product");
            System.out.println("4. View all orders");
            System.out.println("5. Force-update order status");
            System.out.println("6. Logout");
            System.out.print("Choose: ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": viewUsers(); break;
                case "2": viewProducts(); break;
                case "3": deleteProduct(); break;
                case "4": viewOrders(); break;
                case "5": forceUpdateStatus(); break;
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

    private void viewUsers() {
        for (User u : authService.getAllUsers()) {
            System.out.println(u);
        }
    }

    private void viewProducts() {
        List<Product> all = productService.listAll();
        if (all.isEmpty()) {
            System.out.println("No products in the system.");
            return;
        }
        for (int i = 0; i < all.size(); i++) {
            System.out.println(all.get(i));
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

    private void viewOrders() {
        List<Order> all = orderService.listAll();
        if (all.isEmpty()) {
            System.out.println("No orders yet.");
            return;
        }
        for (int i = 0; i < all.size(); i++) {
            System.out.println(all.get(i));
        }
    }

    private void forceUpdateStatus() {
        System.out.print("Order ID: ");
        String oid = sc.nextLine().trim();
        System.out.print("New status (PENDING/CONFIRMED/SHIPPED/DELIVERED/CANCELLED): ");
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
