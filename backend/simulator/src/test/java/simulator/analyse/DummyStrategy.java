package simulator.analyse;

import database.Symbol;
import simulator.bases.StrategyBase;

/**
 * This class is made only to test Account-class, not ment to test any Strategy
 * functionality.
 */

public class DummyStrategy extends StrategyBase {

    public DummyStrategy(Symbol symbol, double cash) {
        super(symbol, cash);

        setLongAllowed(false);
        setShortAllowed(false);
    }

    public void setLongAllowed(boolean longAllowed) { LONG_ALLOWED = longAllowed; }

    public void setShortAllowed(boolean shortAllowed) { SHORT_ALLOWED = shortAllowed; }

    @Override
    public boolean getLongSignal() { return false; }

    @Override
    public boolean getSellSignal() { return false; }

    @Override
    public int getBuyAmount() { return 0; }

    @Override
    public int getSellAmount() { return 0; }

}
