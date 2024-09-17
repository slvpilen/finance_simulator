package broker.connection;

/**
 * Interface for accessing and interacting with an order book.
 */

public interface OrderBook {

        public static final int NUM_LEVELS = 0;

        /**
         * Adds an OrderBookListener to the order book.
         *
         * @param listener the OrderBookListener to be added
         */
        public void addOrderBookListener(
                        OrderBookListner listener);

        public boolean isValid();

        /**
         * Removes an OrderBookListener from the order book.
         *
         * @param listener the OrderBookListener to be removed
         */
        public boolean removeOrderBookListener(
                        OrderBookListner listener);

        /**
         * Notice all registered OrderBookListeners when order book is chenging.
         */
        public void noticeListners();

        /**
         * Gets the bid price at the specified level.
         *
         * @param level the level in the order book
         * @return the bid price at the specified level
         */
        public double getBidPrice(int level);

        /**
         * Gets the ask price at the specified level.
         *
         * @param level the level in the order book
         * @return the ask price at the specified level
         */
        public double getAskPrice(int level);

        /**
         * Gets the bid volume at the specified level.
         *
         * @param level the level in the order book
         * @return the bid volume at the specified level
         */
        public double getBidVolume(int level);

        /**
         * Gets the ask volume at the specified level.
         *
         * @param level the level in the order book
         * @return the ask volume at the specified level
         */
        public double getAskVolume(int level);

        /**
         * Gets the number of bid levels in the order book.
         *
         * @return the number of bid levels
         */
        public int getNumBids();

        /**
         * Gets the number of ask levels in the order book.
         *
         * @return the number of ask levels
         */
        public int getNumAsks();

        /**
         * Gets the mid price, which is the average of the best bid and best ask prices.
         *
         * @return the mid price
         */
        public double getMidPrice();

        /**
         * Gets the spread, which is the difference between the best ask and best bid
         * prices.
         *
         * @return the spread
         */
        public double getSpread();

        public String getTicker();

        public String getSecType();

        public String getCurrency();

        public String getExchange();

        public int getNumLevels();

        /**
         * Gets the best bid price.
         *
         * @return the best bid price
         */
        public double getBestBid();

        /**
         * Gets the best ask price.
         *
         * @return the best ask price
         */
        public double getBestAsk();

        /**
         * Gets the volume of the best bid.
         *
         * @return the volume at the best bid price
         */
        public double getBestBidVolume();

        /**
         * Gets the volume of the best ask.
         *
         * @return the volume at the best ask price
         */
        public double getBestAskVolume();

        /**
         * Gets the volume at a specific price level for either bids or asks.
         *
         * @param price the price level
         * @param isBid true if bid volume is requested, false if ask volume is
         *              requested
         * @return the volume at the specified price level
         */
        public double getVolumeAtPrice(double price,
                        boolean isBid);

        /**
         * Gets the volume at a specific level in the order book for either bids or
         * asks.
         *
         * @param level the level in the order book
         * @param isBid true if bid volume is requested, false if ask volume is
         *              requested
         * @return the volume at the specified level
         */
        public int getVolumeAtLevel(int level, boolean isBid);

        /**
         * Gets the volume at the mid price.
         *
         * @return the volume at the mid price
         */
        public double getVolumeAtMid();

        /**
         * Gets the last update time of the order book.
         *
         * @return the timestamp of the last update
         */
        public long getLastUpdateTime();

        /**
         * Gets the total bid volume across all levels in the order book.
         *
         * @return the total bid volume
         */
        public double getTotalBidVolume();

        /**
         * Gets the total ask volume across all levels in the order book.
         *
         * @return the total ask volume
         */
        public double getTotalAskVolume();

        /**
         * Gets the number of bid price levels in the order book.
         *
         * @return the number of bid price levels
         */
        public int getBidPriceLevels();

        /**
         * Gets the number of ask price levels in the order book.
         *
         * @return the number of ask price levels
         */
        public int getAskPriceLevels();

        /**
         * Gets the bid order details at the specified level.
         *
         * @param level the level in the order book
         * @return the bid order at the specified level
         */
        public Order getBidOrder(int level);

        /**
         * Gets the ask order details at the specified level.
         *
         * @param level the level in the order book
         * @return the ask order at the specified level
         */
        public Order getAskOrder(int level);

        public int getLevelIgnoreVolumeThresholdBid(
                        int volumeThreshold);

        public int getLevelIgnoreVolumeThresholdAsk(
                        int volumeThreshold);

        public OrderBook withoutOrder(Order exludeOrder);
}
