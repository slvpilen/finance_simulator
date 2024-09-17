package simulator.analyse;

import database.Symbol;
import simulator.bases.StrategyBase;

public class ShortPosition extends Position {

    public ShortPosition(Symbol symbol, int amount, double price, StrategyBase strategy, int date) {
        super(symbol, amount, price, strategy, date);
        boolean longOrder = holdings > 0 || amount > 0;
        if (longOrder)
            throw new IllegalArgumentException("ShortPosition cant go long!");
    }

    @Override
    public void handleBuying(int amount, double price) {
        super.handleBuying(amount, price);

        if (holdings > 0)
            throw new IllegalStateException("ShortPosition cant go long!");
    }

    @Override
    public boolean hasPosition() { return holdings < 0; }

}
