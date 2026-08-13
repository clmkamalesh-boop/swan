package capstone.model;

public class Buyer extends User {
    private String shippingAddress;

    public Buyer(String userId, String username, String passwordHash, String email, String shippingAddress) {
        super(userId, username, passwordHash, email, Role.BUYER);
        this.shippingAddress = shippingAddress;
    }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
}
