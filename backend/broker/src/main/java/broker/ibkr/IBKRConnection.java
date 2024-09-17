package broker.ibkr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.ib.client.*;

import broker.connection.BrokerConnection;
import broker.connection.OrderBook;

public class IBKRConnection
                implements EWrapper, BrokerConnection {

        private final EClientSocket clientSocket;
        private final EJavaSignal signal;
        private final String accountName = "U12301058";
        private static int nextTickerId = 1;
        private int nextOrderId;

        private Map<Integer, OrderBookImpl> orderBooks;

        public IBKRConnection() {
                signal = new EJavaSignal();
                clientSocket = new EClientSocket(this, signal);
                connect();
                orderBooks = new HashMap<>();
        }

        // public static void main(String[] args) {
        // IBKRConnection conn = new IBKRConnection();
        // conn.connect();
        // conn.requestMarketDepth();
        // }

        public void connect() {
                clientSocket.eConnect("127.0.0.1", 7496, 0); // 7497 is the default port for TWS, 4001 for IB Gateway
                final int SLEEP_TIME = 1000;
                final EReader reader = new EReader(clientSocket,
                                signal);
                reader.start();

                new Thread(() -> {
                        while (clientSocket.isConnected()) {
                                signal.waitForSignal();
                                try {
                                        reader.processMsgs();
                                } catch (Exception e) {
                                        System.out.println(e
                                                        .getMessage());
                                }
                        }
                }).start();

                try {
                        Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                        e.printStackTrace();
                }
        }

        /*
         * Request the order book for the given stock. The order book will be returned
         * when it is complete. (i.e. all levels have been received)
         */
        @Override
        public OrderBook getOrderBook(String ticker,
                        String secType, String currency,
                        String exchange) {
                OrderBookImpl orderBook = requestMarketDepth(
                                ticker, secType, currency,
                                exchange);

                int counter = 0;
                while (!orderBook.isOrderBookComplete()) {
                        if (counter > 1000) {
                                System.out.println(
                                                "Order book not complete after 100 tries.");
                                break;
                        }
                        try {
                                Thread.sleep(50);
                        } catch (InterruptedException e) {
                                e.printStackTrace();
                        }
                        counter++;
                }
                // It is no guarantee that the order book is complete after 1000 tries.
                // But you will get noticed if it is not.

                return orderBook;
        }

        private OrderBookImpl requestMarketDepth(String ticker,
                        String secType, String currency,
                        String exchange) {
                Contract contract = new Contract();

                contract.symbol(ticker);
                contract.secType(secType);
                contract.currency(currency);
                contract.exchange(exchange);

                OrderBookImpl orderBook = new OrderBookImpl(
                                ticker, secType, currency,
                                exchange);
                orderBooks.put(nextTickerId, orderBook);

                clientSocket.reqMktDepth(nextTickerId, contract,
                                10, false, null);

                nextTickerId++;

                return orderBook;
        }

        boolean isOrderPlaced = false;

        @Override
        public void tickPrice(int tickerId, int field,
                        double price, TickAttrib attrib) {
                if (tickerId == 1 && (field == 1 || field == 2
                                || field == 4)) { // 1 = bid price, 2 = ask price, 4 =
                                                  // last price
                        // if (field == 1)
                        // System.out.println("Bid Price : " + price);
                        // else if (field == 2)
                        // System.out.println("Ask Price : " + price);
                        // else if (field == 4)
                        // System.out.println("Last Price: " + price);
                        // else {
                        // System.out.println(price);
                        // }
                }

                if (nextOrderId <= 0) {
                        return;
                }

                if (!isOrderPlaced) {
                        // placeBuyOrder();
                        // placeSellOrder();
                }
        }

        @Override
        public void updateMktDepth(int tickerId, int position,
                        int operation, int side, double price,
                        Decimal size) {
                String sideStr = (side == 0) ? "Ask" : "Bid";
                String operationStr = "";
                switch (operation) {
                case 0:
                        operationStr = "Insert";
                        break;
                case 1:
                        operationStr = "Update";
                        break;
                case 2:
                        operationStr = "Delete";
                        break;
                }
                OrderBookImpl orderBook = orderBooks
                                .get(tickerId);
                switch (sideStr) {
                case "Ask":
                        orderBook.updateAsk(position, price,
                                        (int) size.longValue());
                        break;

                case "Bid":
                        orderBook.updateBid(position, price,
                                        (int) size.longValue());
                        break;
                }

                if (!orderBook.isOrderBookComplete() && orderBook
                                .hasOrdersInEveryLevel()) {
                        orderBook.handleOrderBookIsComplete();
                        return;
                }

                if (operationStr.equals("Insert")) {
                        orderBook.setOrderBookComplete(false);
                        orderBook.startTimer();
                }

                if (!orderBook.isOrderBookComplete()
                                && operationStr.equals(
                                                "Update")) {
                        orderBook.handleOrderBookIsComplete();
                }
                System.out.println(operationStr);
        }

        @Override
        public void updateMktDepthL2(int tickerId, int position,
                        String marketMaker, int operation,
                        int side, double price, Decimal size,
                        boolean isSmartDepth) {
                updateMktDepth(tickerId, position, operation,
                                side, price, size);
        }

        // Limit order
        @Override
        public boolean placeBuyOrder(String ticker,
                        String secType, String currency,
                        String exchange,
                        broker.connection.Order order) {
                Contract contract = new Contract();
                contract.symbol(ticker);

                contract.secType(secType);
                contract.currency(currency);
                contract.exchange(exchange);

                com.ib.client.Order IBKROrder = new com.ib.client.Order();
                IBKROrder.action("BUY");
                IBKROrder.orderType("LMT");
                IBKROrder.totalQuantity(Decimal
                                .get(order.getQuantity()));
                IBKROrder.lmtPrice(order.getPrice());

                IBKROrder.account(accountName);

                //
                // clientSocket.placeOrder(nextOrderId, contract,
                // IBKROrder);
                isOrderPlaced = true;

                System.out.println("Order placed: ID="
                                + nextOrderId);
                nextOrderId++;

                return true; // TODO: add check order is placed, if not return false
        }

        @Override
        public boolean placeSellOrder(String ticker,
                        String secType, String currency,
                        String exchange,
                        broker.connection.Order order) {
                Contract contract = new Contract();
                contract.symbol(ticker);
                contract.secType(secType);
                contract.currency(currency);
                contract.exchange(exchange);

                com.ib.client.Order IBKROrder = new com.ib.client.Order();
                IBKROrder.action("SELL");
                IBKROrder.orderType("LMT");
                IBKROrder.totalQuantity(Decimal
                                .get(order.getQuantity()));
                IBKROrder.lmtPrice(order.getPrice());
                IBKROrder.account(accountName);

                // Remoce comment to place order
                // clientSocket.placeOrder(nextOrderId, contract,
                // IBKROrder);
                isOrderPlaced = true;

                nextOrderId++;

                System.out.println("Order placed: ID="
                                + nextOrderId);

                return true;
        }

        @Override
        public void tickSize(int tickerId, int field,
                        Decimal size) {}

        @Override
        public void tickOptionComputation(int tickerId,
                        int field, int tickAttrib,
                        double impliedVol, double delta,
                        double optPrice, double pvDividend,
                        double gamma, double vega, double theta,
                        double undPrice) {}

        @Override
        public void tickGeneric(int tickerId, int tickType,
                        double value) {}

        @Override
        public void tickString(int tickerId, int tickType,
                        String value) {}

        @Override
        public void tickEFP(int tickerId, int tickType,
                        double basisPoints,
                        String formattedBasisPoints,
                        double impliedFuture, int holdDays,
                        String futureLastTradeDate,
                        double dividendImpact,
                        double dividendsToLastTradeDate) {}

        @Override
        public void orderStatus(int orderId, String status,
                        Decimal filled, Decimal remaining,
                        double avgFillPrice, int permId,
                        int parentId, double lastFillPrice,
                        int clientId, String whyHeld,
                        double mktCapPrice) {}

        @Override
        public void openOrder(int orderId, Contract contract,
                        com.ib.client.Order order,
                        OrderState orderState) {}

        @Override
        public void openOrderEnd() {}

        @Override
        public void updateAccountValue(String key, String value,
                        String currency, String accountName) {}

        @Override
        public void updatePortfolio(Contract contract,
                        Decimal position, double marketPrice,
                        double marketValue, double averageCost,
                        double unrealizedPNL, double realizedPNL,
                        String accountName) {}

        @Override
        public void updateAccountTime(String timeStamp) {}

        @Override
        public void accountDownloadEnd(String accountName) {}

        @Override
        public void nextValidId(int orderId) {
                nextOrderId = orderId;
                System.out.println("Next Valid Order ID: "
                                + orderId);
        }

        @Override
        public void contractDetails(int reqId,
                        ContractDetails contractDetails) {}

        @Override
        public void bondContractDetails(int reqId,
                        ContractDetails contractDetails) {}

        @Override
        public void contractDetailsEnd(int reqId) {}

        @Override
        public void execDetails(int reqId, Contract contract,
                        Execution execution) {}

        @Override
        public void execDetailsEnd(int reqId) {}

        @Override
        public void updateNewsBulletin(int msgId, int msgType,
                        String message, String origExchange) {}

        @Override
        public void managedAccounts(String accountsList) {}

        @Override
        public void receiveFA(int faDataType, String xml) {}

        @Override
        public void historicalData(int reqId, Bar bar) {}

        @Override
        public void scannerParameters(String xml) {}

        @Override
        public void scannerData(int reqId, int rank,
                        ContractDetails contractDetails,
                        String distance, String benchmark,
                        String projection, String legsStr) {}

        @Override
        public void scannerDataEnd(int reqId) {}

        @Override
        public void realtimeBar(int reqId, long time,
                        double open, double high, double low,
                        double close, Decimal volume,
                        Decimal wap, int count) {}

        @Override
        public void currentTime(long time) {}

        @Override
        public void fundamentalData(int reqId, String data) {}

        @Override
        public void deltaNeutralValidation(int reqId,
                        DeltaNeutralContract deltaNeutralContract) {}

        @Override
        public void tickSnapshotEnd(int reqId) {}

        @Override
        public void marketDataType(int reqId,
                        int marketDataType) {}

        @Override
        public void commissionReport(
                        CommissionReport commissionReport) {}

        @Override
        public void position(String account, Contract contract,
                        Decimal pos, double avgCost) {}

        @Override
        public void positionEnd() {}

        @Override
        public void accountSummary(int reqId, String account,
                        String tag, String value,
                        String currency) {}

        @Override
        public void accountSummaryEnd(int reqId) {}

        @Override
        public void verifyMessageAPI(String apiData) {}

        @Override
        public void verifyCompleted(boolean isSuccessful,
                        String errorText) {}

        @Override
        public void verifyAndAuthMessageAPI(String apiData,
                        String xyzChallenge) {}

        @Override
        public void verifyAndAuthCompleted(boolean isSuccessful,
                        String errorText) {}

        @Override
        public void displayGroupList(int reqId, String groups) {}

        @Override
        public void displayGroupUpdated(int reqId,
                        String contractInfo) {}

        @Override
        public void error(Exception e) { e.printStackTrace(); }

        @Override
        public void error(String str) {
                System.err.println(str);
        }

        @Override
        public void error(int id, int errorCode, String errorMsg,
                        String advancedOrderRejectJson) {
                System.err.println(EWrapperMsgGenerator.error(id,
                                errorCode, errorMsg,
                                advancedOrderRejectJson));
        }

        @Override
        public void connectionClosed() {
                System.out.println("Connection closed.");
        }

        @Override
        public void connectAck() {}

        @Override
        public void positionMulti(int reqId, String account,
                        String modelCode, Contract contract,
                        Decimal pos, double avgCost) {}

        @Override
        public void positionMultiEnd(int reqId) {}

        @Override
        public void accountUpdateMulti(int reqId, String account,
                        String modelCode, String key,
                        String value, String currency) {}

        @Override
        public void accountUpdateMultiEnd(int reqId) {}

        @Override
        public void securityDefinitionOptionalParameter(
                        int reqId, String exchange,
                        int underlyingConId, String tradingClass,
                        String multiplier,
                        Set<String> expirations,
                        Set<Double> strikes) {}

        @Override
        public void securityDefinitionOptionalParameterEnd(
                        int reqId) {}

        @Override
        public void softDollarTiers(int reqId,
                        SoftDollarTier[] tiers) {}

        @Override
        public void familyCodes(FamilyCode[] familyCodes) {}

        @Override
        public void symbolSamples(int reqId,
                        ContractDescription[] contractDescriptions) {}

        @Override
        public void historicalDataEnd(int reqId,
                        String startDateStr,
                        String endDateStr) {}

        @Override
        public void mktDepthExchanges(
                        DepthMktDataDescription[] depthMktDataDescriptions) {}

        @Override
        public void tickNews(int tickerId, long timeStamp,
                        String providerCode, String articleId,
                        String headline, String extraData) {}

        @Override
        public void smartComponents(int reqId,
                        Map<Integer, Entry<String, Character>> theMap) {}

        @Override
        public void tickReqParams(int tickerId, double minTick,
                        String bboExchange,
                        int snapshotPermissions) {}

        @Override
        public void newsProviders(
                        NewsProvider[] newsProviders) {}

        @Override
        public void newsArticle(int requestId, int articleType,
                        String articleText) {}

        @Override
        public void historicalNews(int requestId, String time,
                        String providerCode, String articleId,
                        String headline) {}

        @Override
        public void historicalNewsEnd(int requestId,
                        boolean hasMore) {}

        @Override
        public void headTimestamp(int reqId,
                        String headTimestamp) {}

        @Override
        public void histogramData(int reqId,
                        List<HistogramEntry> items) {}

        @Override
        public void historicalDataUpdate(int reqId, Bar bar) {}

        @Override
        public void rerouteMktDataReq(int reqId, int conId,
                        String exchange) {}

        @Override
        public void rerouteMktDepthReq(int reqId, int conId,
                        String exchange) {}

        @Override
        public void marketRule(int marketRuleId,
                        PriceIncrement[] priceIncrements) {}

        @Override
        public void pnl(int reqId, double dailyPnL,
                        double unrealizedPnL,
                        double realizedPnL) {}

        @Override
        public void pnlSingle(int reqId, Decimal pos,
                        double dailyPnL, double unrealizedPnL,
                        double realizedPnL, double value) {}

        @Override
        public void historicalTicks(int reqId,
                        List<com.ib.client.HistoricalTick> ticks,
                        boolean done) {}

        @Override
        public void historicalTicksBidAsk(int reqId,
                        List<com.ib.client.HistoricalTickBidAsk> ticks,
                        boolean done) {}

        @Override
        public void historicalTicksLast(int reqId,
                        List<com.ib.client.HistoricalTickLast> ticks,
                        boolean done) {}

        @Override
        public void tickByTickAllLast(int reqId, int tickType,
                        long time, double price, Decimal size,
                        TickAttribLast tickAttribLast,
                        String exchange,
                        String specialConditions) {}

        @Override
        public void tickByTickBidAsk(int reqId, long time,
                        double bidPrice, double askPrice,
                        Decimal bidSize, Decimal askSize,
                        TickAttribBidAsk tickAttribBidAsk) {}

        @Override
        public void tickByTickMidPoint(int reqId, long time,
                        double midPoint) {}

        @Override
        public void orderBound(long orderId, int apiClientId,
                        int apiOrderId) {}

        @Override
        public void completedOrder(Contract contract,
                        com.ib.client.Order order,
                        OrderState orderState) {}

        @Override
        public void completedOrdersEnd() {}

        @Override
        public void replaceFAEnd(int reqId, String text) {}

        @Override
        public void wshMetaData(int reqId, String dataJson) {}

        @Override
        public void wshEventData(int reqId, String dataJson) {}

        @Override
        public void historicalSchedule(int reqId,
                        String startDateTime, String endDateTime,
                        String timeZone,
                        List<com.ib.client.HistoricalSession> sessions) {}

        @Override
        public void userInfo(int reqId,
                        String whiteBrandingId) {}

}
