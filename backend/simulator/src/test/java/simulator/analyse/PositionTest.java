package simulator.analyse;

import simulator.bases.StrategyBase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import database.DatabaseService;
import database.Symbol;

public class PositionTest {

    Symbol eqnr;
    Symbol dno;
    StrategyBase strategy;
    Position position;

    @BeforeEach
    void setup() {
        eqnr = DatabaseService.getSymbol("EQNR");
        dno = DatabaseService.getSymbol("DNO");
        strategy = new DummyStrategy(eqnr, 100000);
        position = new Position(eqnr, 10, 100.0, strategy, 20240305);
    }

    @Test
    @DisplayName("Illegal construcors")
    void testConstructurInputs() {
        assertThrows(IllegalArgumentException.class, () -> new Position(eqnr, 1, 0, strategy, 20240305));
        assertThrows(IllegalArgumentException.class, () -> new Position(eqnr, 1, -100.0, strategy, 20240305));
        // create this test for long only
        // assertThrows(IllegalArgumentException.class, () -> new Position("EQNR", -1,
        // 100.0, strategy, true));
    }

    @Test
    @DisplayName("Holdings during scaling in")
    void testScalingIn() {
        assertEquals(10, position.getHoldings(), "wrong holdings made by constructore");
        position.addPosition(10, 200.0);
        assertEquals(20, position.getHoldings(), "adding position fail");
        position.addPosition(2, 200.0);
        assertEquals(22, position.getHoldings(), "adding position fail");
        position.addPosition(1, 200.0);
        assertEquals(23, position.getHoldings(), "adding position fail");
        assertThrows(IllegalArgumentException.class, () -> position.addPosition(1, -200.0),
                "adding negative price, but wrong exception");
    }

    @Test
    @DisplayName("AVG-price during scaling (long)")
    void testScalingInAVGPrice() {
        assertEquals(100, position.getAveragePrice(), "wrong price");
        position.addPosition(10, 200.0);
        assertEquals(150, position.getAveragePrice(), "wrong avg-price");
        position.addPosition(10, 300.0);
        assertEquals(200, position.getAveragePrice(), "wrong avg-price");
        position.addPosition(30, 400.0);
        assertEquals(300, position.getAveragePrice(), "wrong avg-price");
        position.addPosition(60, 300.1);
        assertEquals(300.05, position.getAveragePrice(), "wrong avg-price");
    }

    @Test
    @DisplayName("avg-price and holding: scaling out")
    void testScalingOUT() {
        assertEquals(10, position.getHoldings());
        assertEquals(100, position.getAveragePrice(), "wrong avg-price");
        position.addPosition(-1, 200.0);
        assertEquals(9, position.getHoldings(), "scaling out fail");
        assertEquals(100, position.getAveragePrice(), "wrong avg-price");
        position.addPosition(-2, 200.0);
        assertEquals(7, position.getHoldings(), "scaling out fail");
        assertEquals(100, position.getAveragePrice(), "wrong avg-price");
        position.addPosition(-3, 200.0);
        assertEquals(4, position.getHoldings(), "scaling out fail");
        assertEquals(100, position.getAveragePrice(), "wrong avg-price");
    }

    @Test
    @DisplayName("Last buy price when buying")
    void testLastBuyPrice() { assertEquals(100, position.getLastBuyPrice(), "Wrong price"); }

    @Test
    @DisplayName("shorting scaling in and cover")
    void testShorting() {
        Position shortPosition = new Position(dno, -10, 100, strategy, 20240305);
        assertEquals(100, shortPosition.getAveragePrice(), "wrong avg-price");
        assertEquals(-10, shortPosition.getHoldings(), "wrong holdings");
        shortPosition.addPosition(-10, 110);
        assertEquals(105, shortPosition.getAveragePrice(), "wrong avg-price");
        assertEquals(-20, shortPosition.getHoldings(), "wrong holdings");
        shortPosition.addPosition(20, 110);
        assertEquals(0, shortPosition.getHoldings(), "wrong holdings");
    }

    @Test
    @DisplayName("flipping direct from short to long is not allowed")
    void testShorting2() {
        Position shortPosition = new Position(eqnr, -10, 100, strategy, 20240305);
        assertThrows(IllegalStateException.class, () -> shortPosition.addPosition(12, 100),
                "Flipping from short to long not ok");
    }

    @Test
    @DisplayName("from short thrue zero then 'flipping' is OK")
    void testShortingThenGoThruZeroThenLong() {
        // Position shortPosition = new Position("AAPL", -10, 100, strategy);
        position.addPosition(-10, 110);
        position.addPosition(20, 100);
        position.addPosition(20, 200);
        assertEquals(150, position.getAveragePrice(),
                "when a position go from short thru zero then long should have new avgPrice");
    }

    @Test
    @DisplayName("from short thrue zero then long is OK")
    void testShortPositionOnlyShort() {
        Position shortPosition = new ShortPosition(eqnr, -10, 100, strategy, 20240305);
        shortPosition.addPosition(-10, 110);
        assertThrows(IllegalStateException.class, () -> {
            shortPosition.addPosition(30, 100);
        }, "ShortPosition can only be short or 0!");

    }

    @Test
    @DisplayName("from short thrue zero then long is OK")
    void testLongPositionOnlyLong() {
        Position shortPosition = new LongPosition(eqnr, 10, 100, strategy, 20240305);
        shortPosition.addPosition(-10, 110);
        assertThrows(IllegalStateException.class, () -> {
            shortPosition.addPosition(-30, 100);
        }, "ShortPosition can only be short or 0!");

    }

}
