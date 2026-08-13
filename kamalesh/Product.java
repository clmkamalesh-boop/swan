package capstone.model;

public class Product {
    private String productId;
    private String name;
    private String description;
    private String category;
    private double price;
    private int stock;
    private String sellerId;

    public Product(String productId, String name, String description, String category,
                    double price, int stock, String sellerId) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.sellerId = sellerId;
    }

    public String getProductId() { return productId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public String getSellerId() { return sellerId; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setCategory(String category) { this.category = category; }
    public void setPrice(double price) { this.price = price; }
    public void setStock(int stock) { this.stock = stock; }

    // called on order confirm/cancel, kept here since stock is product-owned state
    public void decreaseStock(int qty) { this.stock -= qty; }
    public void increaseStock(int qty) { this.stock += qty; }

    @Override
    public String toString() {
        return String.format("%s | %-20s | $%-8.2f | stock:%-4d | %s", productId, name, price, stock, category);
    }
}
