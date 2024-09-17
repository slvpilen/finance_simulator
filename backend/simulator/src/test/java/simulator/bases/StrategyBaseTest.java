package simulator.bases;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import database.Symbol;
import simulator.analyse.Position;
import database.TickData;

public class StrategyBaseTest {

    List<TickData> tickDatas;
    StrategyBase strategy;

    @BeforeEach
    void setup() {
        tickDatas = new ArrayList<>();
        tickDatas.add(new TickData(20240101, 100, 103, 99, 100,
                100, 10000));
        tickDatas.add(new TickData(20240102, 100, 102, 100, 99,
                90, 12000));
        Symbol ekorness = new Symbol(0, "EKORNESS", "Ekrns",
                null, null, null, null);
        strategy = new StrategyBaseDemoImpl(ekorness, 100000);

    }

    @Test
    @DisplayName("Independent open position")
    void testClonePosition() {
        StrategyBase clone = strategy.clone();

        strategy.setOpenPosition(
                new Position(strategy.getSymbol(), 100, 1,
                        strategy, 20240101));
        assertEquals(100,
                strategy.getOpenPosition().getHoldings(),
                "wrong holdings");
        assertEquals(null, clone.getOpenPosition(),
                "wrong clone open position");
    }

    @Test
    @DisplayName("Dependent symbol")
    void testSymbolDependentColone() {
        StrategyBase clone = strategy.clone();
        assertEquals(strategy.getSymbol(), clone.getSymbol(),
                "wrong symbol");
    }
}