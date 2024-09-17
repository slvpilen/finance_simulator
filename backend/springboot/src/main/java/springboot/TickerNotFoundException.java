package springboot;

public class TickerNotFoundException extends RuntimeException {

    public TickerNotFoundException() { super("Invalid ticker"); }

    public TickerNotFoundException(String message) {
        super(message);
    }

}
