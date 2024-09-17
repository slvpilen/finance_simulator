package simulator.bases;

import java.util.ArrayList;
import java.util.List;

import database.Symbol;
import simulator.analyse.AnalyseListener;
import simulator.analyse.Position;
import utils.Date;
import lombok.Getter;

/*
 * This is the abstract class for all strategies. It contains all the methods
 * that are common for all strategies. It also implements the strategy
 * interface, which contains all the methods that are common for all
 * strategies. The abstract class also implements the AnalyseListener interface,
 * which contains the methods that are used to update information about the
 * current date and cash in the simulation.
 */
public abstract class StrategyBase implements AnalyseListener {

    @Getter
    protected Symbol symbol;
    @Getter
    protected int date;
    @Getter
    private double cash;
    protected Position openPosition;

    protected boolean LONG_ALLOWED, SHORT_ALLOWED;
    protected static int MINIMUM_DATA_REQUIRED = 1; // Checking if there is enough data to run the strategy day by day
    private List<AnalyseListener> analyseListners = new ArrayList<>();

    // TODO: remove cash from constructor
    public StrategyBase(Symbol symbol, double cash) {

        validateInput(symbol, cash);

        this.symbol = symbol;
        this.cash = cash;
    }

    private void validateInput(Symbol symbol, double cash) {
        if (symbol == null) {
            throw new IllegalArgumentException("symbol is null");
        }

        List<Integer> dateList = symbol.getDateList();
        if (dateList.isEmpty())
            throw new IllegalStateException("dateList for: "
                    + symbol + " has noe dates!");

        if (cash < 0)
            throw new IllegalArgumentException(
                    "Cash needs to be positive!");

    }

    public List<Integer> getDates() {
        return symbol.getDateList();
    }

    public void iterationDate(int date, double cash) {
        this.cash = cash;
        this.date = date;
        analyseListners.forEach(
                listner -> listner.iterationDate(date, cash));
    }

    public void beforeAllIteration() {}

    public void beforeAllIteration(
            List<StrategyBase> strategys) {}

    public void beforeAllIterationOnlyOne() {}

    public void beforeEachTick() {}

    // This is used to only trigger one strategy/symbol per tick. Ide is for
    // distrubuting calculation
    public void beforeEachTickOnlyOne() {}

    public void afterEachTick() {}

    public void setOpenPosition(Position openPosition) {
        this.openPosition = openPosition;
    }

    public Position getOpenPosition() { return openPosition; }

    public abstract boolean getLongSignal();

    public abstract boolean getSellSignal();

    // Long strategy is most common, that's why this is 'defalut' false
    public boolean getShortSignal() { return false; }

    public boolean getCoverSignal() { return false; }

    public double getBuyPrice() { return getCurrentPrice(); }

    public double getSellPrice() { return getCurrentPrice(); }

    public double getShortPrice() { return getCurrentPrice(); }

    public double getCoverPrice() { return getCurrentPrice(); }

    public double getCurrentPrice() {
        return symbol.getClose(date);
    }

    public abstract int getBuyAmount();

    public abstract int getSellAmount();

    public String getSymbolName() { return symbol.getName(); }

    public int getInnehav() {
        if (openPosition == null)
            return 0;
        return openPosition.getHoldings();
    }

    public boolean isLongAllowed() { return LONG_ALLOWED; }

    public boolean isShortAllowed() { return SHORT_ALLOWED; }

    public boolean hasEnoughhData() {
        return symbol.hasData(date, MINIMUM_DATA_REQUIRED);
    }

    public double getOpenPositionValue() {
        if (openPosition == null)
            return 0;
        return openPosition.getPositionValue();
    }

    public String toString() {
        return "Strategy [symbol=" + symbol + "]";
    }

    protected void addAnalyseListner(AnalyseListener listner) {
        analyseListners.add(listner);
    }

    // Often used in strategies
    public int getMonthNumber() {
        return Date.extractMonth(getDate());
    }

    public int getDayOfMonth() {
        return Date.extractDayOfMonth(getDate());
    }

    public int getYearNumber() {
        return Date.extractYear(getDate());
    }

    public int getDayOfWeek() {
        return Date.extractDayOfWeek(getDate());
    }

    public int getNumberOfTradingDaysSinceLatestBuy() {
        if (openPosition == null)
            return -1;
        return getNumberOfTradingDaysBetweenDates(
                openPosition.getLastBuy(), date);
    }

    public int getNumberOfTradingDaysSinceLatestSell() {
        if (openPosition == null)
            return -1;
        return getNumberOfTradingDaysBetweenDates(
                openPosition.getLastSell(), date);
    }

    private int getNumberOfTradingDaysBetweenDates(int first,
            int last) {
        int indexOfLast = symbol.getTickDatas().indexOf(last);
        int indexOfFirst = symbol.getTickDatas().indexOf(first);
        return indexOfLast - indexOfFirst;
    }

    public double getLastBuyPrice() {
        if (openPosition == null) {
            return 0;
        } else {
            return openPosition.getLastBuyPrice();
        }
    }

    @Override
    public StrategyBase clone() {

        return this.clone();
    }
}
