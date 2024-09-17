package broker.adaptivePosition;

import broker.connection.Order;

import broker.connection.BrokerConnection;
import broker.connection.OrderBook;
import lombok.Getter;

@Getter
public class DummyBrokerConnection implements BrokerConnection {

        private Order latestOrder;

        // public DummyBrokerConnection() {
        // latestOrder = Order.builder().price(-1)
        // .quantity(-1).build();
        // }

        @Override
        public OrderBook getOrderBook(String ticker,
                        String secType, String currency,
                        String exchange) {
                throw new UnsupportedOperationException(
                                "Unimplemented method 'getOrderBook'");
        }

        @Override
        public boolean placeBuyOrder(String ticker,
                        String secType, String currency,
                        String exchange, Order order) {
                latestOrder = order;
                return true;
        }

        @Override
        public boolean placeSellOrder(String ticker,
                        String secType, String currency,
                        String exchange, Order order) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException(
                                "Unimplemented method 'placeSellOrder'");
        }

}
