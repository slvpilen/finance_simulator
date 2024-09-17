package broker.connection;

public interface BrokerConnection {

        public OrderBook getOrderBook(String ticker, String secType, String currency, String exchange);

        public boolean placeBuyOrder(String ticker, String secType, String currency, String exchange, Order order);

        public boolean placeSellOrder(String ticker, String secType, String currency, String exchange, Order order);

}
