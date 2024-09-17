package broker.ibkr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import broker.adaptivePosition.DummyBrokerConnection;
import broker.adaptivePosition.TopOfBookPosition;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderBookImplTest {

    TopOfBookPosition topOfBookPosition;
    OrderBookImpl orderBook;
    DummyBrokerConnection brokerConnection;

    @BeforeEach
    void setup() {
        orderBook = new OrderBookImpl("lipi", "STK", "SEK",
                "SFB");

        orderBook.updateBid(0, 97, 200);
        orderBook.updateBid(1, 96, 100);
        orderBook.updateBid(2, 95, 100);
        orderBook.updateBid(3, 94, 100);
        orderBook.updateBid(4, 93, 100);

        orderBook.updateAsk(0, 105, 100);
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    @DisplayName("Should return level 0")
    void levelIgnoreVolumeThresholdBidTest() {
        int level = orderBook
                .getLevelIgnoreVolumeThresholdBid(200);

        assertEquals(0, level, "Level zero has quantity 200");
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    @DisplayName("Clone should be independent")
    void cloneIndependentTest() {
        OrderBookImpl orderBookClone = orderBook.clone();

        orderBookClone.updateBid(0, 98, 100);

        double originalPrice = orderBook.getBidOrder(0)
                .getPrice();
        double clonePrice = orderBookClone.getBidOrder(0)
                .getPrice();

        assertEquals(97, originalPrice,
                "Price should not change in original order book");

        assertEquals(98, clonePrice,
                "Price should change in clone");
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    @DisplayName("Higest at level 0 and zero at end")
    void sortBidsTest() {

        orderBook.updateBid(0, 0, 0);
        orderBook.sortBids();

        double bidPrice = orderBook.getBidOrder(0).getPrice();
        assertEquals(96, bidPrice,
                "Previous level 1 should not be level 0");

        double bidPriceZero = orderBook.getBidOrder(4)
                .getPrice();
        assertEquals(0, bidPriceZero,
                "Price back of book should be 0");
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    @DisplayName("Howest at level 0 and zero at end")
    void sortAsksTest() {
        orderBook.updateAsk(1, 106, 100);
        orderBook.updateAsk(2, 107, 100);
        orderBook.updateAsk(3, 108, 100);
        orderBook.updateAsk(4, 109, 100);

        orderBook.updateAsk(0, 0, 0);
        orderBook.sortAsks();

        double askPrice = orderBook.getAskOrder(0).getPrice();
        assertEquals(106, askPrice,
                "Previous level 1 should not be level 0");

        double askPriceZero = orderBook.getAskOrder(4)
                .getPrice();
        assertEquals(0, askPriceZero,
                "Price back of book should be 0");
    }
}
