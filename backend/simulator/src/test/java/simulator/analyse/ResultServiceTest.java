package simulator.analyse;

import simulator.bases.StrategyBase;
import simulator.result.ResultService;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import database.DatabaseService;
import database.Symbol;

public class ResultServiceTest {

    Symbol eqnr;
    StrategyBase strategy;
    Position position;
    ResultService resultService;

    @BeforeEach
    void setup() {
        Symbol eqnr = DatabaseService.getSymbol("EQNR"); // TODO: Mock this
        strategy = new DummyStrategy(eqnr, 100000);
        position = new Position(eqnr, 10, 100.0, strategy, 20240305);
        resultService = new ResultService();
    }

    @Test
    @DisplayName("number of trades during scaling (long)")
    void testScalingInAVGPrice() {
        position.addPosition(10, 200.0);
        position.addPosition(10, 300.0);
        position.addPosition(30, 400.0);
        position.addPosition(60, 300.0);
        position.addPosition(-90, 100);
        resultService.addClosedTrade(position, -90);
        assertEquals(1, resultService.getNumberOfClosedTrades(), "wrong numbers of trades");
    }

    @Test
    @DisplayName("hitrate when scaling in (long)")
    void testHitrate() {
        position.addPosition(10, 200.0);
        position.addPosition(10, 300.0);
        position.addPosition(30, 400.0);
        position.addPosition(60, 500.1);
        position.addPosition(-90, 600);
        resultService.addClosedTrade(position, -90);

        Position position2 = new Position(eqnr, 10, 100.0, strategy, 20240305);
        position2.addPosition(10, 300.0);
        position2.addPosition(30, 400.0);
        position2.addPosition(60, 500.0);
        position2.addPosition(-90, 50);
        resultService.addClosedTrade(position2, -90);
        assertEquals(0.5, resultService.getStatsHitrate(), "one winning trade and one loosing should give 0.5");
    }

    @Test
    @DisplayName("procent increas each trade")
    void testDTTrade() {
        position.addPosition(10, 200.0);
        position.addPosition(10, 200.0);
        position.addPosition(10, 100.0);
        position.addPosition(-30, 300);
        resultService.addClosedTrade(position, -30); // 100% increase

        Position position2 = new Position(eqnr, 10, 200.0, strategy, 20240305);
        position2.addPosition(10, 200.0);
        position2.addPosition(10, 400.0);
        position2.addPosition(10, 400.0);
        position2.addPosition(-40, 150);
        resultService.addClosedTrade(position2, -40); // -50%

        Position position3 = new Position(eqnr, 10, 100.0, strategy, 20240305);
        position3.addPosition(10, 100.0);
        position3.addPosition(10, 100.0);
        position3.addPosition(10, 100.0);
        position3.addPosition(-40, 200);
        resultService.addClosedTrade(position3, -40); // 100%

        Position position4 = new Position(eqnr, 10, 100.0, strategy, 20240305);
        position4.addPosition(-10, 100.0);
        resultService.addClosedTrade(position4, -10); // flat

        assertEquals(37.5, resultService.getStatsIncreasDTrades(),
                "wrong increase/trade after 4 trades (2 win, 1 loose, 1 flat)");
        assertEquals(0.0, resultService.getLastTradeIncrease(), "wrong increase/trade for latest trade");
    }
}
