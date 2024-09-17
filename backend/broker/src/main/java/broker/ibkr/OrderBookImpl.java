package broker.ibkr;

import java.util.ArrayList;
import java.util.List;

import broker.connection.Order;
import broker.connection.OrderBook;
import broker.connection.OrderBookAuthor;
import broker.connection.OrderBookListner;
import lombok.Getter;
import lombok.Setter;
import java.util.Timer;
import java.util.TimerTask;

public class OrderBookImpl
        implements OrderBook, OrderBookAuthor {

    private static final int NUM_LEVELS = 5;

    private Order[] bids = new Order[NUM_LEVELS];
    private Order[] asks = new Order[NUM_LEVELS];

    private List<OrderBookListner> listeners = new ArrayList<>();

    @Getter
    @Setter
    private boolean isOrderBookComplete;

    private static final int INACTIVITY_THRESHOLD_MS = 5000;
    private Timer timerSinceOrderbookInsertion = new Timer();

    @Getter
    private final IBKRStock ibkrStock;

    public OrderBookImpl(String ticker, String secType,
            String currency, String exchange) {
        ibkrStock = IBKRStock.builder().ticker(ticker)
                .secType(secType).currency(currency)
                .exchange(exchange).build();

        for (int i = 0; i < NUM_LEVELS; i++) {
            bids[i] = Order.builder().price(0).quantity(0)
                    .isBid(true).build();
            asks[i] = Order.builder().price(0).quantity(0)
                    .isBid(false).build();
        }
    }

    public void updateBid(int level, double price,
            int quantity) {
        if ((price == 0 && quantity != 0)
                || (price > 0 && quantity == 0)) {
            throw new IllegalArgumentException(
                    "Illegal price amount combination. Price: "
                            + price + " Quantity: " + quantity);
        }
        bids[level].setPrice(price);
        bids[level].setQuantity(quantity);
        noticeListners();
    }

    public void updateAsk(int level, double price,
            int quantity) {
        if ((price == 0 && quantity != 0)
                || (price > 0 && quantity == 0)) {
            throw new IllegalArgumentException(
                    "Illegal price amount combination. Price: "
                            + price + " Quantity: " + quantity);
        }
        asks[level].setPrice(price);
        asks[level].setQuantity(quantity);
        noticeListners();
    }

    protected boolean hasOrdersInEveryLevel() {
        for (int i = 0; i < NUM_LEVELS; i++) {
            if (bids[i].getQuantity() == 0
                    || asks[i].getQuantity() == 0) {
                return false;
            }
        }
        return true;
    }

    protected void startTimer() {
        cancelTimer(); // Cancel any existing timer
        timerSinceOrderbookInsertion = new Timer();

        timerSinceOrderbookInsertion.schedule(new TimerTask() {
            @Override
            public void run() { handleOrderBookIsComplete(); }
        }, INACTIVITY_THRESHOLD_MS);
    }

    /*
     * This method is called when the order book is complete inserting data. Because
     * one level is inserted at a time.
     */
    protected void handleOrderBookIsComplete() {
        cancelTimer();
        setOrderBookComplete(true);
        System.out.println("Order book is complete.");
    }

    protected void cancelTimer() {
        if (timerSinceOrderbookInsertion == null) {
            return;
        }
        timerSinceOrderbookInsertion.cancel();
        timerSinceOrderbookInsertion = null;
    }

    @Override
    public String getTicker() { return ibkrStock.getTicker(); }

    @Override
    public String getSecType() { return ibkrStock.getSecType(); }

    @Override
    public String getCurrency() {
        return ibkrStock.getCurrency();
    }

    @Override
    public String getExchange() {
        return ibkrStock.getExchange();
    }

    @Override
    public void addOrderBookListener(OrderBookListner listener) {
        listeners.add(listener);
    }

    @Override
    public boolean removeOrderBookListener(
            OrderBookListner listener) {
        return listeners.remove(listener);
    }

    @Override
    public void noticeListners() {
        listeners.forEach(
                listener -> listener.onOrderBookUpdate());
    }

    // Showing 5 decimals
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order book for" + ibkrStock + "\n");
        sb.append("Bids:\n");
        for (int i = 0; i < NUM_LEVELS; i++) {
            sb.append(String.format(
                    "Level %d: Price = %.5f, Volume = %d \n", i,
                    bids[i].getPrice(), bids[i].getQuantity()));
        }
        sb.append("Asks:\n");
        for (int i = 0; i < NUM_LEVELS; i++) {
            sb.append(String.format(
                    "Level %d: Price = %.5f, Volume = %d \n", i,
                    asks[i].getPrice(), asks[i].getQuantity()));
        }
        return sb.toString();
    }

    @Override
    public int getNumLevels() { return NUM_LEVELS; }

    @Override
    public double getBidPrice(int level) {
        return bids[level].getPrice();
    }

    @Override
    public double getAskPrice(int level) {
        return asks[level].getPrice();
    }

    @Override
    public double getBidVolume(int level) {
        return bids[level].getQuantity();
    }

    @Override
    public double getAskVolume(int level) {
        return asks[level].getQuantity();
    }

    @Override
    public int getNumBids() { // TODO Auto-generated method stub
        throw new UnsupportedOperationException(
                "Unimplemented method 'getNumBids'");
    }

    @Override
    public int getNumAsks() { // TODO Auto-generated method stub
        throw new UnsupportedOperationException(
                "Unimplemented method 'getNumAsks'");
    }

    @Override
    public double getMidPrice() { // TODO Auto-generated method stub
        throw new UnsupportedOperationException(
                "Unimplemented method 'getMidPrice'");
    }

    @Override
    public double getSpread() { // TODO Auto-generated method stub
        throw new UnsupportedOperationException(
                "Unimplemented method 'getSpread'");
    }

    @Override
    public double getBestBid() { return bids[0].getPrice(); }

    @Override
    public double getBestAsk() { return asks[0].getPrice(); }

    @Override
    public double getBestBidVolume() { // TODO Auto-generated method stub
        throw new UnsupportedOperationException(
                "Unimplemented method 'getBestBidVolume'");
    }

    @Override
    public double getBestAskVolume() { // TODO Auto-generated method stub
        throw new UnsupportedOperationException(
                "Unimplemented method 'getBestAskVolume'");
    }

    @Override
    public double getVolumeAtPrice(double price, boolean isBid) { // TODO Auto-generated method stub
        throw new UnsupportedOperationException(
                "Unimplemented method 'getVolumeAtPrice'");
    }

    @Override
    public int getVolumeAtLevel(int level, boolean isBid) { // TODO Auto-generated method stub
        if (isBid) {
            return bids[level].getQuantity();
        } else {
            return asks[level].getQuantity();
        }
    }

    @Override
    public double getVolumeAtMid() { // TODO Auto-generated method stub
        throw new UnsupportedOperationException(
                "Unimplemented method 'getVolumeAtMid'");
    }

    @Override
    public long getLastUpdateTime() { // TODO Auto-generated method stub
        throw new UnsupportedOperationException(
                "Unimplemented method 'getLastUpdateTime'");
    }

    @Override
    public double getTotalBidVolume() {
        double total = 0;
        for (int i = 0; i < NUM_LEVELS; i++) {
            total += bids[i].getQuantity();
        }
        return total;
    }

    // TODO: rename getTotalAskQuantity?
    @Override
    public double getTotalAskVolume() {
        double total = 0;
        for (int i = 0; i < NUM_LEVELS; i++) {
            total += asks[i].getQuantity();
        }
        return total;
    }

    public boolean isValid() {
        boolean hasAsk = asks[0].getQuantity() > 0; // != -1?
        boolean hasBid = bids[0].getQuantity() > 0; // != -1?
        return hasAsk && hasBid;
    }

    @Override
    public int getBidPriceLevels() { // TODO Auto-generated method stub
        throw new UnsupportedOperationException(
                "Unimplemented method 'getBidPriceLevels'");
    }

    @Override
    public int getAskPriceLevels() { // TODO Auto-generated method stub
        throw new UnsupportedOperationException(
                "Unimplemented method 'getAskPriceLevels'");
    }

    @Override
    public Order getBidOrder(int level) { return bids[level]; }

    @Override
    public Order getAskOrder(int level) { return asks[level]; }

    @Override
    public int getLevelIgnoreVolumeThresholdBid(
            int volumeThreshold) {
        int volume = 0;
        for (int i = 0; i < NUM_LEVELS; i++) {
            volume += this.getVolumeAtLevel(i, true);
            if (volume >= volumeThreshold) {
                return i;
            }
        }
        return -1; // No level has enough volume
    }

    @Override
    public int getLevelIgnoreVolumeThresholdAsk(
            int volumeThreshold) {
        int volume = 0;
        for (int i = 0; i < NUM_LEVELS; i++) {
            volume += this.getVolumeAtLevel(i, false);
            if (volume >= volumeThreshold) {
                return i;
            }
        }
        return -1; // No level has enough volume
    }

    /*
     * If a order is completly removed it will be quantity 0, even if its in middle
     * of book
     */
    @Override
    public OrderBook withoutOrder(Order exludeOrder) {
        OrderBookImpl orderBookClone = this.clone();

        Order order;
        for (int i = 0; i < NUM_LEVELS; i++) {
            if (exludeOrder.isBid()) {
                order = orderBookClone.bids[i];
            } else {
                order = orderBookClone.asks[i];
            }
            if (order.getPrice() == exludeOrder.getPrice()) {
                if (order.getQuantity() > exludeOrder
                        .getQuantity()) {
                    throw new IllegalArgumentException(
                            "The order to exclude has a higher quantity than the order in the order book");
                }
                order.setQuantity(
                        orderBookClone.bids[i].getQuantity()
                                - exludeOrder.getQuantity());
                if (order.getQuantity() == 0) {
                    order.setPrice(0);
                }
                if (order.getQuantity() > 0) {
                    throw new IllegalArgumentException(
                            "The order to exclude has a higher quantity than the order in the order book");
                }
            }
        }
        if (exludeOrder.isBid()) {
            orderBookClone.sortBids();
        } else {
            orderBookClone.sortAsks();
        }
        return orderBookClone;
    }

    protected void sortBids() {
        for (int i = 0; i < NUM_LEVELS; i++) {
            for (int j = i + 1; j < NUM_LEVELS; j++) {
                if (bids[i].getPrice() == 0
                        || (bids[j].getPrice() != 0
                                && bids[i].getPrice() < bids[j]
                                        .getPrice())) {
                    Order temp = bids[i];
                    bids[i] = bids[j];
                    bids[j] = temp;
                }
            }
        }
    }

    protected void sortAsks() {
        for (int i = 0; i < NUM_LEVELS; i++) {
            for (int j = i + 1; j < NUM_LEVELS; j++) {
                if (asks[i].getPrice() == 0
                        || (asks[j].getPrice() != 0
                                && asks[i].getPrice() > asks[j]
                                        .getPrice())) {
                    Order temp = asks[i];
                    asks[i] = asks[j];
                    asks[j] = temp;
                }
            }
        }
    }

    /*
     * The listners are NOT cloned
     */
    @Override
    public OrderBookImpl clone() {
        OrderBookImpl clone = new OrderBookImpl(this.getTicker(),
                this.getSecType(), this.getCurrency(),
                this.getExchange());
        for (int i = 0; i < NUM_LEVELS; i++) {
            clone.bids[i].setPrice(this.bids[i].getPrice());
            clone.bids[i]
                    .setQuantity(this.bids[i].getQuantity());

            clone.asks[i].setPrice(this.asks[i].getPrice());
            clone.asks[i]
                    .setQuantity(this.asks[i].getQuantity());
        }

        clone.listeners = new ArrayList<>();

        return clone;
    }

}
