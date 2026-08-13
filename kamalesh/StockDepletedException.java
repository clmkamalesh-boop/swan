package capstone.exception;

public class StockDepletedException extends RuntimeException {
    public StockDepletedException(String message) {
        super(message);
    }
}
