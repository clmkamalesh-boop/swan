package capstone.ui;

import capstone.model.Role;
import capstone.model.User;
import capstone.repository.OrderRepository;
import capstone.repository.ProductRepository;
import capstone.repository.UserRepository;
import capstone.service.AuthService;
import capstone.service.OrderService;
import capstone.service.ProductService;
import java.util.Scanner;

public class AppController {
    private final Scanner sc;
    private final AuthService authService;
    private final ProductService productService;
    private final OrderService orderService;

    private final AuthMenu authMenu;
    private final AdminMenu adminMenu;
    private final SellerMenu sellerMenu;
    private final BuyerMenu buyerMenu;

    public AppController() {
        this.sc = new Scanner(System.in);

        UserRepository userRepo = new UserRepository();
        ProductRepository productRepo = new ProductRepository();
        OrderRepository orderRepo = new OrderRepository();

        this.authService = new AuthService(userRepo);
        this.productService = new ProductService(productRepo);
        this.orderService = new OrderService(orderRepo, productRepo, userRepo);

        this.authMenu = new AuthMenu(sc, authService);
        this.adminMenu = new AdminMenu(sc, authService, productService, orderService);
        this.sellerMenu = new SellerMenu(sc, authService, productService, orderService);
        this.buyerMenu = new BuyerMenu(sc, authService, productService, orderService);

        seedDemoData(userRepo);
    }

    // gives graders something to log into immediately without registering first
    private void seedDemoData(UserRepository userRepo) {
        authService.registerAdmin("admin", "admin123", "admin@skillswap.dev");
        authService.registerSeller("devseller", "pass1234", "dev@skillswap.dev", "CodeWorks Studio");
        authService.registerBuyer("founder1", "pass1234", "founder1@startup.dev", "123 Market St, Delaware");
        authService.logout(); // registration auto-does not log in, but stay explicit

        User seller = userRepo.findByUsername("devseller");
        productService.addProduct(seller, "Delaware C-Corp Incorporation", "Full incorporation service", "Legal", 1200.00, 5);
        productService.addProduct(seller, "SaaS Privacy Policy Draft", "Custom privacy policy", "Legal", 300.00, 10);
        productService.addProduct(seller, "Landing Page Build", "Responsive marketing landing page", "Design", 450.00, 8);
    }

    public void start() {
        System.out.println("Demo accounts -> admin/admin123 | devseller/pass1234 | founder1/pass1234");
        boolean running = true;

        while (running) {
            User loggedIn = authMenu.run();
            if (loggedIn == null) {
                running = false;
                continue;
            }
            routeToDashboard(loggedIn);
        }

        System.out.println("Goodbye.");
        sc.close();
    }

    private void routeToDashboard(User user) {
        Role role = user.getRole();
        if (role == Role.ADMIN) {
            adminMenu.run();
        } else if (role == Role.SELLER) {
            sellerMenu.run();
        } else if (role == Role.BUYER) {
            buyerMenu.run();
        }
    }
}
