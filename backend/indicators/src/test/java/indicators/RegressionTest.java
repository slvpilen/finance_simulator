package indicators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RegressionTest {

    Regression regression;

    @BeforeEach
    void setup() {

        // Symbol eqnr = DatabaseService.getSymbol("EQNR");

        // strategy = new StrategyTestLongAndShort(eqnr, 100000);
        // account = new Account(100000);
        // account.addStrategy(strategy);
    }

    @Test
    @DisplayName("Regression testing beta")
    void testConstructurInputs() {
        // assertThrows(IllegalArgumentException.class, () -> new Account(-10),
        // "negative cash!");
    }

}
