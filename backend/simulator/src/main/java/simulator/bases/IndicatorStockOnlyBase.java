package simulator.bases;

import database.Symbol;
import simulator.analyse.AnalyseListener;

public abstract class IndicatorStockOnlyBase implements Indicator, AnalyseListener {

    protected Symbol symbol;
    protected int date;

    public IndicatorStockOnlyBase(Symbol symbol) { this.symbol = symbol; }

    public abstract double getValue();

    public void iterationDate(int date, double cash) { this.date = date; }

}
