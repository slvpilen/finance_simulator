package strategies.connorsAi;

import database.Symbol;
import indicators.ROC;
import indicators.RelativeStrengthIndex;
import indicators.utils.TimeFrame;
import simulator.bases.StrategyBase;
import socket.SocketClient;
import strategies.connorsAi.aiModel.ModelATraingDataFactory;

public class ConnorsAiStrategy extends StrategyBase {
    public static final String STRATEGY_NAME = "connors-ai";

    ROC roc;
    ModelATraingDataFactory modelATraingDataFactory;

    RelativeStrengthIndex rsiDaily;
    RelativeStrengthIndex rsiWeekly;
    RelativeStrengthIndex rsiMonthly;

    public ConnorsAiStrategy(Symbol symbol, double cash) {
        super(symbol, cash);

        LONG_ALLOWED = true;
        SHORT_ALLOWED = true;
        MINIMUM_DATA_REQUIRED = 5;

        modelATraingDataFactory = new ModelATraingDataFactory(
                symbol);

        roc = new ROC(symbol, 1);

        rsiDaily = new RelativeStrengthIndex(symbol, 14);
        rsiWeekly = new RelativeStrengthIndex(symbol, 14);
        rsiMonthly = new RelativeStrengthIndex(symbol, 14);

        rsiWeekly.setTimeFrame(TimeFrame.WEEKLY);
        rsiMonthly.setTimeFrame(TimeFrame.MONTHLY);

        addAnalyseListner(roc);
        addAnalyseListner(rsiDaily);
        addAnalyseListner(rsiWeekly);
        addAnalyseListner(rsiMonthly);

    }

    @Override
    public void beforeAllIteration() {
        modelATraingDataFactory.createTraingData();
    }

    int counter = 0;

    @Override
    public void beforeEachTick() {
        // Fit the model every 220th tick
        if (counter % 500 == 0) {
            modelATraingDataFactory.fit(getDate());
        }
        counter++;

        int dayOfMonth = getDayOfMonth();
        int dayOfWeek = getDayOfWeek();
        double roc1 = roc.getValue(1);
        double roc2 = roc.getValue(2);
        double roc3 = roc.getValue(3);
        double roc4 = roc.getValue(4);
        double roc5 = roc.getValue(5);
        double rsi_daily = rsiDaily.getValue();
        double rsi_weekly = rsiWeekly.getValue();
        double rsi_monthly = rsiMonthly.getValue();

        predictionPercent = SocketClient.getModelAPrediction(
                dayOfWeek, dayOfMonth, roc1, roc2, roc3, roc4,
                roc5, rsi_daily, rsi_weekly, rsi_monthly);

    }

    @Override
    public boolean getLongSignal() {

        boolean edge = predictionPercent > 1.2;// 2.5 btc; // sp500 1 dag 0.3
        // edge &= getDayOfMonth() > 22;

        boolean hasNoLongPosition = getInnehav() == 0;
        boolean hasCashAvailable = getCash() > getBuyPrice();

        boolean isLongSignal = edge && hasNoLongPosition
                && hasCashAvailable;

        return isLongSignal;

    }

    private final int EXIT_TRADE_AFTER_DAYS = 1;

    @Override
    public boolean getSellSignal() {
        boolean sellEdge;
        try {
            sellEdge = getNumberOfTradingDaysSinceLatestBuy() >= EXIT_TRADE_AFTER_DAYS;
        } catch (Exception e) {
            return false;
        }
        sellEdge &= predictionPercent < 1.5; // 2 btc // sp500 1dag: 0.25

        boolean hasInnehav = getInnehav() > 0;
        boolean isSellSignal = hasInnehav && sellEdge;

        return isSellSignal;
    }

    double predictionPercent;

    @Override
    public boolean getShortSignal() {

        boolean edge = predictionPercent < -1.5;// -4,5 btc; sp500 1dag -1

        boolean hasNoInnehav = getInnehav() == 0;
        boolean hasCashAvailable = getCash() > getShortPrice();

        boolean isShortSignal = edge && hasNoInnehav
                && hasCashAvailable;

        return isShortSignal;

    }

    @Override
    public boolean getCoverSignal() {
        boolean coverEdge;
        try {
            coverEdge = getNumberOfTradingDaysSinceLatestSell() >= EXIT_TRADE_AFTER_DAYS;
        } catch (Exception e) {
            return false;
        }

        coverEdge &= predictionPercent > -0.7; // btc: -2 // sp500 1dag: -0.3

        boolean hasInnehav = getInnehav() < 0;
        boolean isCoverSignal = hasInnehav && coverEdge;

        return isCoverSignal;
    }

    @Override
    public int getSellAmount() {
        int innehav = getInnehav();
        if (innehav > 0) {
            return innehav;
        }
        int maxSellAmount = (int) (getCash() / getSellPrice());

        if (predictionPercent > -2) {
            return (int) (maxSellAmount / 3);
        }
        if (predictionPercent > -3) {
            return (int) (maxSellAmount / 2);
        }
        if (predictionPercent > -4) {
            return (int) (maxSellAmount / 1.5);
        }
        return maxSellAmount;

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
        int innehav = getInnehav();
        if (innehav < 0) {
            return -innehav;
        }
        int maxBuyAmount = (int) (getCash() / getBuyPrice());
        if (predictionPercent < 1) {
            return (int) (maxBuyAmount / 3);
        }
        if (predictionPercent < 2) {
            return (int) (maxBuyAmount / 2);
        }
        if (predictionPercent < 3) {
            return (int) (maxBuyAmount / 1.5);
        }
        if (predictionPercent < 4) {
            return (int) (maxBuyAmount / 1.2);
        }
        return maxBuyAmount;
    }

}
