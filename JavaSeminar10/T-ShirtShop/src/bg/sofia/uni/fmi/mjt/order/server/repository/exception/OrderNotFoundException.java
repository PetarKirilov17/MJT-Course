package bg.sofia.uni.fmi.mjt.order.server.repository.exception;

public class OrderNotFoundException extends Exception {

    public OrderNotFoundException(String message) {
        super(message);
    }

    public OrderNotFoundException(String message, Throwable e) {
        super(message, e);
    }
}