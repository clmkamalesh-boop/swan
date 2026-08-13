package capstone.repository;

import capstone.model.Order;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class OrderRepository {
    private final Map<String, Order> orders = new LinkedHashMap<>();
    private final ReentrantLock lock = new ReentrantLock();
    private int nextId = 1;

    public String generateOrderId() {
        lock.lock();
        try {
            String id = "O" + String.format("%04d", nextId);
            nextId++;
            return id;
        } finally {
            lock.unlock();
        }
    }

    public void save(Order o) {
        lock.lock();
        try {
            orders.put(o.getOrderId(), o);
        } finally {
            lock.unlock();
        }
    }

    public Order findById(String orderId) {
        return orders.get(orderId);
    }

    public Map<String, Order> getAll() {
        return orders;
    }
}
