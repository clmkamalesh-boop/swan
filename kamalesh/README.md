# SkillSwap Marketplace — Capstone

A multi-role CLI application (Admin / Seller / Buyer) with authentication,
role-based dashboards, product CRUD, and an order/transaction workflow with
stock-aware escrow-style checkout.

## Folder structure

```
capstone/
  src/capstone/
    Main.java                     entry point
    model/                        User, Admin, Seller, Buyer, Product, Order, OrderItem, Role, OrderStatus
    exception/                    AuthenticationException, UnauthorizedAccessException,
                                   StockDepletedException, ProductNotFoundException,
                                   OrderNotFoundException, DuplicateUserException, InvalidInputException
    repository/                   UserRepository, ProductRepository, OrderRepository (in-memory, thread-safe)
    service/                      AuthService, ProductService, OrderService (business logic + validation)
    ui/                           AuthMenu, AdminMenu, SellerMenu, BuyerMenu, AppController (role-restricted menus)
```

## Build & run

Requires a JDK (17+ recommended, no external dependencies).

```
cd capstone
javac -d out $(find src -name "*.java")
java -cp out capstone.Main
```

## Demo accounts (seeded on startup)

| Role   | Username   | Password  |
|--------|------------|-----------|
| Admin  | admin      | admin123  |
| Seller | devseller  | pass1234  |
| Buyer  | founder1   | pass1234  |

Three sample products are pre-listed under `devseller`.

## Notes

- Data is in-memory only (repository layer uses `LinkedHashMap` + `ReentrantLock`);
  restarting the app resets state. Swapping in file or JDBC persistence only
  requires reimplementing the repository classes — services and UI are unaffected.
- Passwords are hashed with a simplified demo hash (not for production use).
- Order placement validates every cart line before committing any stock changes,
  so a failed line never partially decrements inventory.
