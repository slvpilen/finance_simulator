package broker.connection;

public interface OrderBookAuthor {

    public void updateBid(int level, double price, int quantity);

    public void updateAsk(int level, double price, int quantity);
}
