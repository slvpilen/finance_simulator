package broker.adaptivePosition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import broker.connection.Order;
import broker.ibkr.OrderBookImpl;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TopOfBookPositionTest {

        TopOfBookPosition topOfBookPosition;
        OrderBookImpl orderBook;
        DummyBrokerConnection brokerConnection;

        @BeforeEach
        void setup() {
                orderBook = new OrderBookImpl("lipi", "STK",
                                "SEK", "SFB");

                orderBook.updateBid(0, 97, 200);
                orderBook.updateBid(1, 96, 100);
                orderBook.updateBid(2, 95, 100);
                orderBook.updateBid(3, 94, 100);
                orderBook.updateBid(4, 93, 100);

                orderBook.updateAsk(0, 105, 100);

                brokerConnection = new DummyBrokerConnection();

                topOfBookPosition = TopOfBookPosition
                                .topOfBookPositionBuilder()
                                .isBid(true).minPrice(96)
                                .maxPrice(102)
                                .ignoreQuantityThreshold(200)
                                .maxPositionValue(1000)
                                .tickSize(0.5)
                                .brokerConnection(
                                                brokerConnection)
                                .orderBook(orderBook).build();

        }

        @Test
        @org.junit.jupiter.api.Order(1)
        @DisplayName("Bid lower then min price")
        void bestBidLowerThenMinPriceTest() {
                topOfBookPosition = TopOfBookPosition
                                .topOfBookPositionBuilder()
                                .isBid(true).minPrice(98)
                                .maxPrice(102)
                                .ignoreQuantityThreshold(200)
                                .maxPositionValue(1000)
                                .tickSize(0.5)
                                .brokerConnection(
                                                brokerConnection)
                                .orderBook(orderBook).build();

                orderBook.addOrderBookListener(
                                topOfBookPosition);
                topOfBookPosition.onOrderBookUpdate(); // Init is also a update

                Order latestOrder = brokerConnection
                                .getLatestOrder();

                assertEquals(null, latestOrder,
                                "Should not place order");
        }

        @Test
        @org.junit.jupiter.api.Order(2)
        @DisplayName("Order at best bid, when no own order allready placed")
        void orderAtBestBidTest() {

                orderBook.addOrderBookListener(
                                topOfBookPosition);
                topOfBookPosition.onOrderBookUpdate(); // Trigger topOfBookPosition

                Order latestOrder = brokerConnection
                                .getLatestOrder();

                assertNotNull(latestOrder,
                                "Should have placed a order");

                assertEquals(97.5, latestOrder.getPrice(),
                                "Should be order top of book pluss one tick size");
        }

        @Test
        @org.junit.jupiter.api.Order(3)
        @DisplayName("Testing ignoreVolumeThreshold")
        void ignoreVolumeThresholdTest() {

                orderBook.updateBid(0, 97, 199);
                orderBook.addOrderBookListener(
                                topOfBookPosition);

                topOfBookPosition.onOrderBookUpdate(); // Init is also a update

                Order latestOrder = brokerConnection
                                .getLatestOrder();

                assertNotNull(latestOrder,
                                "Should have placed a order");

                assertEquals(96.5, latestOrder.getPrice(),
                                "Should add order at level 1 pluss one tick size");
        }

        @Test
        @org.junit.jupiter.api.Order(4)
        @DisplayName("Testing ignoreVolumeThreshold")
        void ignoreVolumeThresholdTest2() {
                orderBook.updateBid(0, 97, 199);
                orderBook.updateBid(1, 96.5, 100);

                orderBook.addOrderBookListener(
                                topOfBookPosition);
                topOfBookPosition.onOrderBookUpdate(); // Init is also a update

                Order latestOrder = brokerConnection
                                .getLatestOrder();

                assertNotNull(latestOrder,
                                "Should have placed a order");

                assertEquals(97, latestOrder.getPrice(),
                                "Should add order at level 1 pluss one tick size => level 0");
        }

        @Test
        @org.junit.jupiter.api.Order(5)
        @DisplayName("Update bid triggers order")
        void orderBookListnerTest() {

                orderBook.updateBid(0, 97, 199);
                orderBook.addOrderBookListener(
                                topOfBookPosition);

                orderBook.updateBid(1, 96.5, 100);

                Order latestOrder = brokerConnection
                                .getLatestOrder();

                assertNotNull(latestOrder,
                                "Should have placed a order");

                assertEquals(97, latestOrder.getPrice(),
                                "Should add order at level 1 pluss one tick size => level 0");
        }

        @Test
        @org.junit.jupiter.api.Order(6)
        @DisplayName("Don't act when own order is placed in orderBook")
        void orderAlreadyInMarkedTest() {

                orderBook.updateBid(0, 97, 199);
                orderBook.addOrderBookListener(
                                topOfBookPosition);

                orderBook.updateBid(1, 96.5, 100);

                Order latestOrder = brokerConnection
                                .getLatestOrder();

                assertNotNull(latestOrder,
                                "Should have placed a order");

                assertEquals(97, latestOrder.getPrice(),
                                "Should add order at level 1 pluss one tick size => level 0");

                orderBook.updateBid(0, 97, 10);

                Order latestOrder2 = brokerConnection
                                .getLatestOrder();

                assertEquals(latestOrder, latestOrder2,
                                "Should not place a new order when own order is in orderBook");
        }

        @Test
        @org.junit.jupiter.api.Order(7)
        @DisplayName("Don't act when own order is placed in orderBook, even if own order is big")
        void orderAlreadyInMarkedTest2() {

                topOfBookPosition = TopOfBookPosition
                                .topOfBookPositionBuilder()
                                .isBid(true).minPrice(96)
                                .maxPrice(102)
                                .ignoreQuantityThreshold(200)
                                .maxPositionValue(100000)
                                .tickSize(0.5)
                                .brokerConnection(
                                                brokerConnection)
                                .orderBook(orderBook).build();

                orderBook.updateBid(0, 97, 199);
                orderBook.addOrderBookListener(
                                topOfBookPosition);

                orderBook.updateBid(1, 96.5, 100);

                Order latestOrder = brokerConnection
                                .getLatestOrder();

                assertNotNull(latestOrder,
                                "Should have placed a order");

                orderBook.updateBid(0, 97, 1000);

                Order latestOrder2 = brokerConnection
                                .getLatestOrder();

                assertEquals(latestOrder, latestOrder2,
                                "Should not place a new order when own order is in orderBook");
        }

        @Test
        @org.junit.jupiter.api.Order(8)
        @DisplayName("Quantity corresponds to maxPositionValue")
        void quantityTest() {

                orderBook.updateBid(0, 97, 199);
                orderBook.addOrderBookListener(
                                topOfBookPosition);

                orderBook.updateBid(1, 96.5, 100);

                Order latestOrder = brokerConnection
                                .getLatestOrder();

                assertNotNull(latestOrder,
                                "Should have placed a order");

                assertEquals(10, latestOrder.getQuantity(),
                                "1000/97 = 10,31 => 10");

        }

        @Test
        @org.junit.jupiter.api.Order(9)
        @DisplayName("Min price")
        void uncompleteOrderBookTest() {

                orderBook.updateBid(0, 97, 199);
                orderBook.updateBid(1, 0, 0);
                orderBook.updateBid(2, 0, 0);
                orderBook.updateBid(3, 0, 0);
                orderBook.updateBid(4, 0, 0);

                orderBook.addOrderBookListener(
                                topOfBookPosition);
                topOfBookPosition.onOrderBookUpdate(); // Init is also a update

                Order latestOrder = brokerConnection
                                .getLatestOrder();

                assertEquals(latestOrder.getPrice(), 96,
                                "Placed at min price");

        }
}
