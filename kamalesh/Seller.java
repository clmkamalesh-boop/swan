package capstone.model;

public class Seller extends User {
    private String storeName;
    private double totalSalesValue;

    public Seller(String userId, String username, String passwordHash, String email, String storeName) {
        super(userId, username, passwordHash, email, Role.SELLER);
        this.storeName = storeName;
        this.totalSalesValue = 0.0;
    }

    public String getStoreName() { return storeName; }
    public double getTotalSalesValue() { return totalSalesValue; }

    // bumped whenever an order containing this seller's product gets confirmed
    public void addSale(double amount) {
        this.totalSalesValue += amount;
    }
}
