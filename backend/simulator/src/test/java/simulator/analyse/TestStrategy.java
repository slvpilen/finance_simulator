package simulator.analyse;

import java.util.function.IntPredicate;

import database.Symbol;
import lombok.Setter;
import simulator.bases.StrategyBase;

/*
 * A strategy for testing shorting. NOT a real strategy.
 * It short 1 stock and covers 1 stock.
 */
@Setter
public class TestStrategy extends StrategyBase {

    private IntPredicate shortSignal;
    private IntPredicate coverSignal;
    private IntPredicate longSinal;
    private IntPredicate sellSignal;

    public TestStrategy(Symbol symbol, double cash) {
        super(symbol, cash);
        setLongAllowed(true);
        setShortAllowed(true);
        MINIMUM_DATA_REQUIRED = 0;
    }

    @Override
    public boolean getLongSignal() { return longSinal.test(date); }

    @Override
    public boolean getSellSignal() { return sellSignal.test(date); }

    @Override
    public int getBuyAmount() { return 1; }

    @Override
    public int getSellAmount() { return 1; }

    @Override
    public boolean getShortSignal() { return shortSignal.test(date); }

    @Override
    public boolean getCoverSignal() { return coverSignal.test(date); }

    public void setLongAllowed(boolean longAllowed) { LONG_ALLOWED = longAllowed; }

    public void setShortAllowed(boolean shortAllowed) { SHORT_ALLOWED = shortAllowed; }

}
