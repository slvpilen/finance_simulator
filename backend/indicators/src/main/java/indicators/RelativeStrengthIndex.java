package indicators;

import simulator.bases.IndicatorBase;
import utils.HashArray;

import java.util.List;

import database.DatabaseService;
import database.DateValueRetriever;
import database.Symbol;
import indicators.utils.DateConverter;
import indicators.utils.TimeFrame;
import lombok.Setter;

/*
 * Relative Strength Index (RSI)
 * This verson is a version with smoothing. That is a common way to calculate RSI.
 */
public class RelativeStrengthIndex extends IndicatorBase {

    @Setter
    private int length = 14;
    private TimeFrame timeFrame = TimeFrame.DAILY;

    private HashArray<Integer, Double> ticksWeekly;
    private HashArray<Integer, Double> ticksMontly;

    public RelativeStrengthIndex(
            DateValueRetriever dateValueRetriever, int length) {
        super(dateValueRetriever);

        boolean hasData = dateValueRetriever.getDateList()
                .size() > 0;
        if (!hasData) {
            throw new IllegalArgumentException(
                    "DateValueRetriever has no data");
        }
        this.length = length;
    }

    public RelativeStrengthIndex(
            DateValueRetriever dateValueRetriever, int length,
            TimeFrame timeFrame) {
        this(dateValueRetriever, length);
        this.timeFrame = timeFrame;

        if (timeFrame == TimeFrame.WEEKLY) {
            ticksWeekly = generateDateValueRetriever(timeFrame);
        } else if (timeFrame == TimeFrame.MONTHLY) {
            ticksMontly = generateDateValueRetriever(timeFrame);
        }
    }

    public void setTimeFrame(TimeFrame timeFrame) {
        this.timeFrame = timeFrame;
        if (timeFrame == TimeFrame.WEEKLY
                && ticksWeekly == null) {
            ticksWeekly = generateDateValueRetriever(timeFrame);
        } else if (timeFrame == TimeFrame.MONTHLY
                && ticksMontly == null) {
            ticksMontly = generateDateValueRetriever(timeFrame);
        }
    }

    private HashArray<Integer, Double> generateDateValueRetriever(
            TimeFrame timeFrameInput) {
        List<Integer> dateList = dateValueRetriever
                .getDateList();
        List<Integer> convertedDates = DateConverter
                .convertDates(dateList, timeFrameInput);
        HashArray<Integer, Double> generatedDateValueRetriever = new HashArray<>();
        for (int i = 0; i < convertedDates.size(); i++) {
            int date = convertedDates.get(i);
            generatedDateValueRetriever.put(date,
                    dateValueRetriever.getAref(date, 0));
        }
        return generatedDateValueRetriever;
    }

    @Override
    public double getValue(int date) {

        double prevAvgGain = 0;
        double prevAvgLoss = 0;
        // Initialize gain and loss
        int closestDate = getClosestDate(date);

        // "Length minus 1" because "length minus 1" intervalls (e.g. 14 days; => 13
        // intervals)
        for (int i = 0; i < length; i++) {
            double today = getAref(closestDate, i);
            double yesterday = getAref(closestDate, i + 1);
            if (today == -1 || yesterday == -1) {
                // throw new IllegalArgumentException(
                // "Not enough data for RSI calculation");
                // }
                return -1;
            }
            double change = today - yesterday;
            if (change > 0) {
                prevAvgGain += change;
            } else {
                prevAvgLoss -= change;
            }
        }

        prevAvgGain /= length;
        prevAvgLoss /= length;

        double mostRecentChange = getAref(closestDate, 0)
                - getAref(closestDate, 1);
        double gain = mostRecentChange > 0 ? mostRecentChange
                : 0;
        double loss = mostRecentChange < 0 ? -mostRecentChange
                : 0;

        // Apply smoothing
        double avgGain = (prevAvgGain * (length - 1) + gain)
                / length;
        double avgLoss = (prevAvgLoss * (length - 1) + loss)
                / length;

        if (avgLoss == 0) {
            return 100; // Prevent division by zero
        }

        double rs = avgGain / avgLoss;
        double rsi = 100 - (100 / (1 + rs));
        // Round
        return Math.round(rsi * 1000.0) / 1000.0;

    }

    private int getClosestDate(int date) {
        switch (timeFrame) {
        case DAILY:
            return dateValueRetriever.getClosestDate(date);
        case WEEKLY:
            return ticksWeekly.getClosestKey(date);
        case MONTHLY:
            return ticksMontly.getClosestKey(date);
        default:
            throw new IllegalArgumentException(
                    "TimeFrame not supported: " + timeFrame);
        }
    }

    private double getAref(int date, int lookback) {
        switch (timeFrame) {
        case DAILY:
            return dateValueRetriever.getAref(date, lookback);

        case WEEKLY:
            return ticksWeekly.getAref(date, lookback);

        case MONTHLY:
            return ticksMontly.getAref(date, lookback);

        default:
            throw new IllegalArgumentException(
                    "TimeFrame not supported: " + timeFrame);
        }
    }

    public static void main(String[] args) {
        // Demonstration of the RelativeStrengthIndex class
        Symbol equinor = DatabaseService.getSymbol("EQNR");
        RelativeStrengthIndex rsi = new RelativeStrengthIndex(
                equinor, 14);
        rsi.setTimeFrame(TimeFrame.WEEKLY);
        System.out.println("RSI for EQNR:");
        List<Integer> dates = equinor.getDateList();
        for (int date : dates) {
            try {
                System.out.println(
                        date + ": " + rsi.getValue(date));
            } catch (Exception e) {
                System.out.println(date + ": " + e.getMessage());
            }
        }

        DatabaseService.close();
    }
}
