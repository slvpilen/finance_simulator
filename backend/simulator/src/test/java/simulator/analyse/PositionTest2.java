package simulator.analyse;

import simulator.bases.StrategyBase;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import database.DatabaseService;
import database.Symbol;

public class PositionTest2 {

    Symbol eqnr;
    StrategyBase strategy;
    Position position;

    @BeforeEach
    void setup() {
        eqnr = DatabaseService.getSymbol("EQNR");
        strategy = new DummyStrategy(eqnr, 100000);
        position = new Position(eqnr, 10, 100.0, strategy, 20240305);
    }

    @Test
    @DisplayName("Test date correct for trades")
    void testTradeDate() {
        position.iterationDate(20240305, 100000);
        assertEquals(20240305, position.getTradeEvents().get(0).getDate());
    }

    @Test
    @DisplayName("Test date correct for multiple trades")
    void testTradeDateScaling() {
        position.iterationDate(20240306, 100000);
        position.addPosition(100, 95.0);
        // TODO refactore so that getValue would be getDate
        // TODO refactore ClosedTrade to take in Position and use it more elegant
        assertEquals(20240305, position.getTradeEvents().get(0).getDate(),
                "Should be the same as first iteration date");
        assertEquals(20240306, position.getTradeEvents().get(1).getDate(), "Should be the same as last iteration date");
    }
}