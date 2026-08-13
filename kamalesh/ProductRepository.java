package capstone.repository;

import capstone.model.Product;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class ProductRepository {
    private final Map<String, Product> products = new LinkedHashMap<>();
    private final ReentrantLock lock = new ReentrantLock();
    private int nextId = 1;

    public String generateProductId() {
        lock.lock();
        try {
            String id = "P" + String.format("%04d", nextId);
            nextId++;
            return id;
        } finally {
            lock.unlock();
        }
    }

    public void save(Product p) {
        lock.lock();
        try {
            products.put(p.getProductId(), p);
        } finally {
            lock.unlock();
        }
    }

    public Product findById(String productId) {
        return products.get(productId);
    }

    public void delete(String productId) {
        lock.lock();
        try {
            products.remove(productId);
        } finally {
            lock.unlock();
        }
    }

    public Map<String, Product> getAll() {
        return products;
    }
}
