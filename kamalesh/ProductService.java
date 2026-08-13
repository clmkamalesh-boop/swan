package capstone.service;

import capstone.exception.InvalidInputException;
import capstone.exception.ProductNotFoundException;
import capstone.exception.UnauthorizedAccessException;
import capstone.model.Product;
import capstone.model.Role;
import capstone.model.User;
import capstone.repository.ProductRepository;
import java.util.ArrayList;
import java.util.List;

public class ProductService {
    private final ProductRepository productRepo;

    public ProductService(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    public Product addProduct(User actor, String name, String description, String category, double price, int stock) {
        if (actor.getRole() != Role.SELLER && actor.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException("Only sellers or admins can add products.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException("Product name cannot be empty.");
        }
        if (price < 0) {
            throw new InvalidInputException("Price cannot be negative.");
        }
        if (stock < 0) {
            throw new InvalidInputException("Stock cannot be negative.");
        }

        String pid = productRepo.generateProductId();
        Product p = new Product(pid, name, description, category, price, stock, actor.getUserId());
        productRepo.save(p);
        return p;
    }

    public void updateProduct(User actor, String productId, String name, String description,
                               String category, Double price, Integer stock) {
        Product p = productRepo.findById(productId);
        if (p == null) {
            throw new ProductNotFoundException("No product with id " + productId);
        }
        // sellers can only touch their own listings, admin can override anything
        if (actor.getRole() == Role.SELLER && !p.getSellerId().equals(actor.getUserId())) {
            throw new UnauthorizedAccessException("You do not own this product.");
        }
        if (actor.getRole() == Role.BUYER) {
            throw new UnauthorizedAccessException("Buyers cannot modify products.");
        }

        if (name != null && !name.trim().isEmpty()) p.setName(name);
        if (description != null) p.setDescription(description);
        if (category != null) p.setCategory(category);
        if (price != null) {
            if (price < 0) throw new InvalidInputException("Price cannot be negative.");
            p.setPrice(price);
        }
        if (stock != null) {
            if (stock < 0) throw new InvalidInputException("Stock cannot be negative.");
            p.setStock(stock);
        }
    }

    public void deleteProduct(User actor, String productId) {
        Product p = productRepo.findById(productId);
        if (p == null) {
            throw new ProductNotFoundException("No product with id " + productId);
        }
        if (actor.getRole() == Role.SELLER && !p.getSellerId().equals(actor.getUserId())) {
            throw new UnauthorizedAccessException("You do not own this product.");
        }
        if (actor.getRole() == Role.BUYER) {
            throw new UnauthorizedAccessException("Buyers cannot delete products.");
        }
        productRepo.delete(productId);
    }

    public Product getById(String productId) {
        Product p = productRepo.findById(productId);
        if (p == null) {
            throw new ProductNotFoundException("No product with id " + productId);
        }
        return p;
    }

    public List<Product> listAll() {
        return new ArrayList<>(productRepo.getAll().values());
    }

    public List<Product> listBySeller(String sellerId) {
        List<Product> out = new ArrayList<>();
        for (Product p : productRepo.getAll().values()) {
            if (p.getSellerId().equals(sellerId)) {
                out.add(p);
            }
        }
        return out;
    }

    // simple case-insensitive substring search across name and category, imperative on purpose
    public List<Product> search(String keyword) {
        List<Product> results = new ArrayList<>();
        if (keyword == null) keyword = "";
        String kw = keyword.toLowerCase().trim();

        for (Product p : productRepo.getAll().values()) {
            boolean nameMatch = p.getName().toLowerCase().contains(kw);
            boolean catMatch = p.getCategory() != null && p.getCategory().toLowerCase().contains(kw);
            if (nameMatch || catMatch) {
                results.add(p);
            }
        }
        return results;
    }
}
