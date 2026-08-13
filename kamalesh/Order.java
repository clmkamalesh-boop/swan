package capstone.model;

import java.util.List;

public class Order {
    private String orderId;
    private String buyerId;
    private List<OrderItem> items;
    private OrderStatus status;
    private long createdAt;

    public Order(String orderId, String buyerId, List<OrderItem> items) {
        this.orderId = orderId;
        this.buyerId = buyerId;
        this.items = items;
        this.status = OrderStatus.PENDING;
        this.createdAt = System.currentTimeMillis();
    }

    public String getOrderId() { return orderId; }
    public String getBuyerId() { return buyerId; }
    public List<OrderItem> getItems() { return items; }
    public OrderStatus getStatus() { return status; }
    public long getCreatedAt() { return createdAt; }

    public void setStatus(OrderStatus status) { this.status = status; }

    public double getTotalAmount() {
        double total = 0.0;
        for (int i = 0; i < items.size(); i++) {
            total += items.get(i).getLineTotal();
        }
        return total;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order ").append(orderId).append(" [").append(status).append("] buyer:").append(buyerId)
          .append(" total:$").append(String.format("%.2f", getTotalAmount())).append("\n");
        for (int i = 0; i < items.size(); i++) {
            sb.append(items.get(i).toString()).append("\n");
        }
        return sb.toString();
    }
}
