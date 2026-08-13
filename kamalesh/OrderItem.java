package capstone.model;

public class OrderItem {
    private String productId;
    private String productName;
    private String sellerId;
    private int quantity;
    private double unitPrice;

    public OrderItem(String productId, String productName, String sellerId, int quantity, double unitPrice) {
        this.productId = productId;
        this.productName = productName;
        this.sellerId = sellerId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getSellerId() { return sellerId; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }

    public double getLineTotal() { return quantity * unitPrice; }

    @Override
    public String toString() {
        return String.format("  - %s x%d @ $%.2f = $%.2f", productName, quantity, unitPrice, getLineTotal());
    }
}
