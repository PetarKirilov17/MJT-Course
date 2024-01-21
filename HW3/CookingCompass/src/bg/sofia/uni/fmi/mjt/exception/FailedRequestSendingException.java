package bg.sofia.uni.fmi.mjt.exception;

public class FailedRequestSendingException extends Exception {
    public FailedRequestSendingException(String message) {
        super(message);
    }

    public FailedRequestSendingException(String message, Throwable cause) {
        super(message, cause);
    }

}
