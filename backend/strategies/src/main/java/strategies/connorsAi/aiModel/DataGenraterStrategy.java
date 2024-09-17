package strategies.connorsAi.aiModel;

import database.Symbol;
import indicators.ROC;
import indicators.RelativeStrengthIndex;
import indicators.utils.TimeFrame;
import lombok.Getter;
import simulator.bases.StrategyBase;
import socket.modelA.TrainingData;

public class DataGenraterStrategy extends StrategyBase {

    ROC roc;
    RelativeStrengthIndex rsiDaily = new RelativeStrengthIndex(
            symbol, 14);
    RelativeStrengthIndex rsiWeekly = new RelativeStrengthIndex(
            symbol, 14);
    RelativeStrengthIndex rsiMonthly = new RelativeStrengthIndex(
            symbol, 14);

    public DataGenraterStrategy(Symbol symbol, double cash) {
        super(symbol, cash);

        LONG_ALLOWED = true;
        SHORT_ALLOWED = false;
        MINIMUM_DATA_REQUIRED = 5;

        roc = new ROC(symbol, 1);

        addAnalyseListner(roc);
        addAnalyseListner(rsiDaily);
        addAnalyseListner(rsiWeekly);
        addAnalyseListner(rsiMonthly);

        rsiWeekly.setTimeFrame(TimeFrame.WEEKLY);
        rsiMonthly.setTimeFrame(TimeFrame.MONTHLY);

    }

    @Getter
    private TrainingData trainingData = new TrainingData();

    @Override
    public void beforeEachTick() {

        int dayOfMonth = getDayOfMonth();
        int dayOfWeek = getDayOfWeek();
        double roc1 = roc.getValue(1);
        double roc2 = roc.getValue(2);
        double roc3 = roc.getValue(3);
        double roc4 = roc.getValue(4);
        double roc5 = roc.getValue(5);

        double oneDaysLater = roc.getValue(-1);
        double twoDaysLater = roc.getValue(-2);
        double threeDaysLater = roc.getValue(-3);
        double fourDaysLater = roc.getValue(-4);
        double fiveDaysLater = roc.getValue(-5);
        double sixDaysLater = roc.getValue(-6);
        double sevenDaysLater = roc.getValue(-7);
        double eightDaysLater = roc.getValue(-8);
        double nineDaysLater = roc.getValue(-9);
        double tenDaysLater = roc.getValue(-10);
        double elevenDaysLater = roc.getValue(-11);
        double twelveDaysLater = roc.getValue(-12);
        double thirteenDaysLater = roc.getValue(-13);
        double fourteenDaysLater = roc.getValue(-14);
        double fifteenDaysLater = roc.getValue(-15);

        double rsiDailyValue = -1000;
        double rsiWeeklyValue = -1000;
        double rsiMonthlyValue = -1000;

        try {
            rsiDailyValue = rsiDaily.getValue();
        } catch (Exception e) {

        }
        try {
            rsiWeeklyValue = rsiWeekly.getValue();
        } catch (Exception e) {

        }
        try {
            rsiMonthlyValue = rsiMonthly.getValue();
        } catch (Exception e) {

        }
        boolean errorInData = roc1 == -1 || roc2 == -1
                || roc3 == -1 || roc4 == -1 || roc5 == -1
                || oneDaysLater == -1 || twoDaysLater == -1
                || threeDaysLater == -1 || fourDaysLater == -1
                || fiveDaysLater == -1 || sixDaysLater == -1
                || sevenDaysLater == -1 || eightDaysLater == -1
                || nineDaysLater == -1 || tenDaysLater == -1
                || elevenDaysLater == -1 || twelveDaysLater == -1
                || thirteenDaysLater == -1
                || fourteenDaysLater == -1
                || fifteenDaysLater == -1;
        if (!errorInData) {
            trainingData.add(date, dayOfWeek, dayOfMonth, roc1,
                    roc2, roc3, roc4, roc5, oneDaysLater,
                    twoDaysLater, threeDaysLater, fourDaysLater,
                    fiveDaysLater, sixDaysLater, sevenDaysLater,
                    eightDaysLater, nineDaysLater, tenDaysLater,
                    elevenDaysLater, twelveDaysLater,
                    thirteenDaysLater, fourteenDaysLater,
                    fifteenDaysLater, rsiDailyValue,
                    rsiWeeklyValue, rsiMonthlyValue);
        }

    }

    @Override
    public boolean getLongSignal() {

        return false;

    }

    @Override
    public boolean getSellSignal() { return false; }

    @Override
    public boolean getShortSignal() {
        return false;

    }

    @Override
    public boolean getCoverSignal() { return false; }

    @Override
    public int getBuyAmount() { return -1; }

    @Override
    public int getSellAmount() { return -1; }

}
