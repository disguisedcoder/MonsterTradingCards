package at.technikum.application.TradingCards.exception;

public class JsonParserException extends RuntimeException {
    public JsonParserException(String message) {
        super(message);
    }

    public JsonParserException(Throwable cause) {
        super(cause);
    }
}
