package at.technikum.application.TradingCards.exception;

public class InvalidBodyException extends RuntimeException {
    public InvalidBodyException(String message) {
        super(message);
    }

    public InvalidBodyException(Throwable cause) {
        super(cause);
    }
}
