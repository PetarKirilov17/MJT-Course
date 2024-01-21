package bg.sofia.uni.fmi.mjt.exception;

public class ForbiddenErrorException extends Exception {
    public ForbiddenErrorException(String message) {
        super(message);
    }

    public ForbiddenErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
