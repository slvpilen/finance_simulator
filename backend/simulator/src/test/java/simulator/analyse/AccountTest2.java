package simulator.analyse;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import database.DatabaseService;
import database.Symbol;
import simulator.bases.StrategyBase;

public class AccountTest2 {

    StrategyBase strategy;
    Account account;

    @BeforeEach
    void setup() {
        Symbol eqnr = DatabaseService.getSymbol("EQNR");
        strategy = new DummyStrategy(eqnr, 100000);
        account = new Account(100000);
        account.addStrategy(strategy);

        account.addPosition(strategy, 10, 100);
        account.addPosition(strategy, -10, 150); // +50%

        account.addPosition(strategy, 10, 100);
        account.addPosition(strategy, -10, 90); // -10%

    }

    @Test
    @DisplayName("hitrate")
    void testHitrate() {
        account.addPosition(strategy, 10, 100);
        account.addPosition(strategy, 10, 50);
        account.addPosition(strategy, -10, 80);

        account.addPosition(strategy, 10, 100);
        account.addPosition(strategy, 10, 50);
        account.addPosition(strategy, -10, 80);

        assertEquals(0.75, account.getResult().getStatsHitrate(), "wrong hitrate");
    }

    @Test
    @DisplayName("percent/trade")
    void testAddingPostitionCash() {
        account.addPosition(strategy, 10, 100);
        account.addPosition(strategy, -10, 150); // +50%

        account.addPosition(strategy, 10, 100);
        account.addPosition(strategy, -10, 90); // -10%

        assertEquals(20, account.getResult().getStatsIncreasDTrades(), "wrong hitrate");
    }

}
