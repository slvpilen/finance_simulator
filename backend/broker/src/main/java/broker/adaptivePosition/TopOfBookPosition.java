package broker.adaptivePosition;

import broker.connection.BrokerConnection;
import broker.connection.Order;
import broker.connection.OrderBook;
import broker.connection.OrderBookListner;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * It will hunt the best bid and ask prices, but will not be affected by small
 * orders. It will only be affected by large orders (larger then
 * ignoreVolumeThreshold). It will also be affected by the spread. It will also
 * back out of the market if it recognize bots.
 */
/*
 * TODO: håndter delkjøp og delsalg
 */
@Getter
@Builder(builderMethodName = "topOfBookPositionBuilder")
public class TopOfBookPosition implements OrderBookListner {

    final boolean isBid;
    final double minPrice; // Is the minimum price we want to buy for
    final double maxPrice;
    final int ignoreQuantityThreshold;
    final double maxPositionValue; // Max,
    final double tickSize;

    int holdings;

    @Setter
    private Order myCurrentInMarkedOrder;
    private Order myOptimalInMarkedOrder;

    private OrderBook orderBook;
    final BrokerConnection brokerConnection;

    @Override
    public void onOrderBookUpdate() {
        System.out.println(orderBook);

        if (orderBook.isValid() == false) {
            return;
        }

        // First calculate the optimal order
        if (isBid) {
            handleBidPosition();
        } else {
            handleAskPosition();
        }

        // Then place the order

        boolean hasActiveOptimalOrder = (myCurrentInMarkedOrder != null
                && myCurrentInMarkedOrder.getQuantity() != 0
                && myCurrentInMarkedOrder
                        .equals(myOptimalInMarkedOrder));
        if (hasActiveOptimalOrder) {
            return; // Do Nothing
        } else {
            boolean hasOrderInMarked = hasOrderInMarked();
            // TODO: Assuming placeOrder delete the old order
            if (isBid) {
                if (!hasOrderInMarked) {
                    placeBuyOrder(myOptimalInMarkedOrder);
                } else {
                    // has order in marked...
                    // Check if the optimal is equal to the on in marked
                    if (myOptimalInMarkedOrder
                            .equals(myCurrentInMarkedOrder)) {
                        return;// Do nothing
                    } else {
                        // Cancel the current order and place the new one
                        // brokerConnection.cancelOrder(myCurrentInMarkedOrder);
                        // placeBuyOrder(myOptimalInMarkedOrder);
                        replaceBuyOrder(myOptimalInMarkedOrder);
                    }
                }
            } else {
                if (!hasOrderInMarked) {
                    placeSellOrder(myOptimalInMarkedOrder);
                } else {
                    // has order in marked...
                    // Check if the optimal is equal to the on in marked
                    if (myOptimalInMarkedOrder
                            .equals(myCurrentInMarkedOrder)) {
                        return; // Do nothing
                    } else {
                        // Cancel the current order and place the new one
                        // brokerConnection.cancelOrder(myCurrentInMarkedOrder);
                        // placeBuyOrder(myOptimalInMarkedOrder);
                        replaceSellOrder(myOptimalInMarkedOrder);
                    }
                }
            }
        }

    }

    private void replaceSellOrder(
            Order myOptimalInMarkedOrder2) {
        // cancel the current order and place the new one
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException(
                "Unimplemented method 'replaceSellOrder'");
    }

    private void replaceBuyOrder(Order myOptimalInMarkedOrder2) {
        // TODO Auto-generated method stub
        // cancel the current order and place the new one
        throw new UnsupportedOperationException(
                "Unimplemented method 'replaceBuyOrder'");
    }

    private void handleBidPosition() {
        if (hasOrderInMarked()) {
            handleBidIfOrderInMarked();
        }
        if (!hasOrderInMarked()) {
            handleBidIfNoOrderInMarked();
        }
    }

    private void handleBidIfOrderInMarked() {
        OrderBook orderBookWithouMyOrder = orderBook
                .withoutOrder(myCurrentInMarkedOrder);
        handleBidIfNoOrderInMarked(orderBookWithouMyOrder);

    }

    private void handleBidIfNoOrderInMarked() {
        handleBidIfNoOrderInMarked(orderBook);
    }

    private void handleBidIfNoOrderInMarked(
            OrderBook orderBook) {

        double bestBid = orderBook.getBestBid();
        boolean shouldNotAddOrder = bestBid > maxPrice
                || bestBid < minPrice;

        if (shouldNotAddOrder) {
            return;
        }

        int levelIgnoreVolumeThresholdBid = orderBook
                .getLevelIgnoreVolumeThresholdBid(
                        ignoreQuantityThreshold);
        boolean hasEnoughVolume = levelIgnoreVolumeThresholdBid != -1;
        if (!hasEnoughVolume) {
            myOptimalInMarkedOrder = Order.builder()
                    .price(minPrice)
                    .quantity(
                            (int) (maxPositionValue / minPrice))
                    .isBid(true).build();
            return; // No level has enough volume => No support => Do nothing
        }

        double targetPrice = orderBook
                .getBidOrder(levelIgnoreVolumeThresholdBid)
                .getPrice();
        targetPrice += tickSize;

        int quantity = (int) (maxPositionValue / targetPrice);

        myOptimalInMarkedOrder = Order.builder()
                .price(targetPrice).quantity(quantity).build();

    }

    private void handleAskPosition() { // TODO Auto-generated method stub
        if (hasOrderInMarked()) {
            handleAskIfOrderInMarked();
        }
        if (!hasOrderInMarked()) {
            handleAskIfNoOrderInMarked();
        }
    }

    private void handleAskIfNoOrderInMarked() {
        handleAskIfNoOrderInMarked(orderBook);
    }

    private void handleAskIfOrderInMarked() {
        OrderBook orderBookWithouMyOrder = orderBook
                .withoutOrder(myCurrentInMarkedOrder);
        handleAskIfNoOrderInMarked(orderBookWithouMyOrder);
    }

    private void handleAskIfNoOrderInMarked(
            OrderBook orderBookWithouMyOrder) {
        double bestAsk = orderBook.getBestAsk();
        boolean shouldNotAddOrder = bestAsk > maxPrice
                || bestAsk < minPrice;

        if (shouldNotAddOrder) {
            return;
        }

        int levelIgnoreVolumeThresholdAsk = orderBook
                .getLevelIgnoreVolumeThresholdAsk(
                        ignoreQuantityThreshold);
        boolean hasEnoughVolume = levelIgnoreVolumeThresholdAsk != -1;
        if (!hasEnoughVolume) {
            myOptimalInMarkedOrder = Order.builder()
                    .price(minPrice).quantity(holdings)
                    .isBid(false).build();
            return; // No level has enough volume => No support => Do nothing
        }

        double targetPrice = orderBook
                .getAskOrder(levelIgnoreVolumeThresholdAsk)
                .getPrice();
        targetPrice += tickSize;

        int quantity = holdings;

        myOptimalInMarkedOrder = Order.builder()
                .price(targetPrice).quantity(quantity).build();
    }

    private boolean hasOrderInMarked() {
        return myCurrentInMarkedOrder != null;
    }

    /*
     * Place a buy order at the given price and quantity for the stock in the given
     * order book.
     */
    private boolean placeBuyOrder(Order order) {
        myCurrentInMarkedOrder = order;
        return brokerConnection.placeBuyOrder(
                orderBook.getTicker(), orderBook.getSecType(),
                orderBook.getCurrency(), orderBook.getExchange(),
                order);
    }

    /*
     * Place a sell order at the given price and quantity for the stock in the given
     * order book.
     */
    private boolean placeSellOrder(Order order) {
        myCurrentInMarkedOrder = order;
        return brokerConnection.placeSellOrder(
                orderBook.getTicker(), orderBook.getSecType(),
                orderBook.getCurrency(), orderBook.getExchange(),
                order);
    }

}
