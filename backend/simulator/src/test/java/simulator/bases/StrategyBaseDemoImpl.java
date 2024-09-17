package simulator.bases;

import database.Symbol;

public class StrategyBaseDemoImpl extends StrategyBase {

    public StrategyBaseDemoImpl(Symbol symbol, double cash) { super(symbol, cash); }

    @Override
    public boolean getLongSignal() { // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getLongSignal'");
    }

    @Override
    public boolean getSellSignal() { // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSellSignal'");
    }

    @Override
    public int getBuyAmount() { // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBuyAmount'");
    }

    @Override
    public int getSellAmount() { // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSellAmount'");
    }

}
