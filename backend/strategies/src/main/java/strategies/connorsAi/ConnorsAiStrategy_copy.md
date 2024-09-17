package strategies.connorsAi;

import database.Commodity;
import database.DatabaseService;
import database.Symbol;
import indicators.ROC;
import lombok.Setter;
import simulator.bases.StrategyBase;

public class ConnorsAiStrategy extends StrategyBase {
    public static final String STRATEGY_NAME = "connors-ai";

    @Setter
    Settings settings = new Settings();

    // TODO: Add indicators
    // MovingAverage ma200;
    Commodity oil = DatabaseService.getCommodity("BZ=F");
    Symbol eqnr = DatabaseService.getSymbol("EQNR");
    ROC roc;

    public ConnorsAiStrategy(Symbol symbol, double cash) {
        super(symbol, cash);

        LONG_ALLOWED = true;// TODO: Implement
        SHORT_ALLOWED = false;// TODO: Implement
        MINIMUM_DATA_REQUIRED = 5;// TODO: Implement

        roc = new ROC(symbol, 1);

        addAnalyseListner(roc);
        // ma200 = new MovingAverage(symbol, 200);
        // addAnalyseListner(ma200);
    }

    @Override
    public void beforeAllIterationOnlyOne() {
        System.out.print("day_of_week,day_of_month");
        for (int i = 1; i < 6; i++) {
            System.out.print(
                    "," + String.valueOf(i) + "_day_change");
        }
        for (int i = 1; i < 16; i++) {
            System.out.print(
                    "," + String.valueOf(i) + "_days_dater");
        }
        System.out.println();

    }

    // double allTimeHighe = 0;
    // double lowestLow = 0;

    @Override
    public boolean getLongSignal() {

        // boolean isNewAllTimeHigh = symbol
        // .getClose(date) > allTimeHighe;
        // if (isNewAllTimeHigh) {
        // allTimeHighe = symbol.getClose(date);
        // lowestLow = symbol.getClose(date);
        // }

        // double drawDown = 100
        // * (allTimeHighe - symbol.getClose(date))
        // / allTimeHighe;

        // boolean isNewLower = symbol.getClose(date) < lowestLow;
        // if (isNewLower) {
        // lowestLow = symbol.getClose(date);
        // isNewLower = true;
        // System.out.println(
        // drawDown + "," + roc.getValue(-2500));
        // }
        // if (oil.hasData(date) && eqnr.hasData(date)) {

        // System.out.println(oil.getClose(date) + ","
        // + eqnr.getClose(date));
        // }

        System.out.print(getDayOfWeek() + "," + getDayOfMonth());
        for (int i = 1; i < 6; i++) {
            System.out.print("," + roc.getValue(i));
        }
        for (int i = 1; i < 16; i++) {
            System.out.print("," + roc.getValue(-i));
        }
        System.out.println();

        return false;
    }

    // TODO:Implement
    @Override
    public boolean getSellSignal() {

        // boolean sellEdge = date > sellDate;

        // boolean hasInnehav = getInnehav() > 0;
        // boolean isSellSignal = hasInnehav && sellEdge;

        // return isSellSignal;
        return false;
    }

    @Override
    public boolean getShortSignal() { return false; }

    @Override
    public boolean getCoverSignal() {

        return false;
    }

    @Override
    public int getSellAmount() {
        int innehav = getInnehav();
        if (innehav > 0) {
            return innehav;
        } else {
            return (int) (getCash() / getBuyPrice());
        }
    }

    @Override
    public double getCurrentPrice() {
        if (symbol.getClose(date) == 0)
            throw new IllegalArgumentException(
                    "no current price:" + symbol + " date: "
                            + date);

        return super.getCurrentPrice();
    }

    @Override
    public double getBuyPrice() {
        double tradingFeePercent = 0.15;
        return super.getBuyPrice()
                * (1 + tradingFeePercent / 100);
    }

    @Override
    public double getSellPrice() {
        double tradingFeePercent = 0.15;
        return super.getBuyPrice()
                * (1 - tradingFeePercent / 100);
    }

    @Override
    public double getShortPrice() {
        double tradingFeePercent = 0.05;
        return super.getBuyPrice()
                * (1 - tradingFeePercent / 100);
    }

    @Override
    public double getCoverPrice() {
        double tradingFeePercent = 0.05;
        return super.getBuyPrice()
                * (1 + tradingFeePercent / 100);
    }

    @Override
    public int getBuyAmount() {
        return (int) (getCash() / getBuyPrice());
    }

}
