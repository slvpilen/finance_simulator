package simulator.analyse;

import database.Symbol;
import simulator.bases.StrategyBase;

public class LongPosition extends Position {

    public LongPosition(Symbol symbol, int amount, double price, StrategyBase strategy, int date) {
        super(symbol, amount, price, strategy, date);
        boolean shortOrder = holdings < 0 || amount < 0;
        if (shortOrder)
            throw new IllegalArgumentException("LongPosition cant go short!");
    }

    @Override
    protected void handleSelling(int amount, double price) {
        super.handleSelling(amount, price);
        if (this.holdings < 0)
            throw new IllegalShortException("not legal go short with this account");
    }

    @Override
    public boolean hasPosition() { return holdings > 0; }

}
