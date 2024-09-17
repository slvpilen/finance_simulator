package indicators;

import simulator.bases.IndicatorBase;
import database.DateValueRetriever;

/*
 * Rate of Change (ROC)
 */

public class ROC extends IndicatorBase {

    private int length;
    // private TimeFrame timeFrame = TimeFrame.DAILY;

    public ROC(DateValueRetriever dateValueRetriever,
            int length) {
        super(dateValueRetriever);
        this.length = length;
    }

    // public void setTimeFrame(TimeFrame timeFrame) { this.timeFrame = timeFrame; }

    @Override
    public double getValue() { return getValue(date, length); }

    public double getValue(int length) {
        if (length == 0) {
            throw new IllegalArgumentException(
                    "Length not set.");
        }
        if (date == 0) {
            throw new IllegalArgumentException("Date not set.");
        }
        return getValue(date, length);
    }

    // TODO: handle not date better. Maybe check for yesterday instead of today
    public double getValue(int date, int length) {
        try {
            double arefClose = dateValueRetriever.getAref(date,
                    length);
            double close = dateValueRetriever.getClose(date);
            double roc = 0;
            if (length > 0) {
                roc = ((close - arefClose) / arefClose) * 100;
            } else {
                roc = ((arefClose - close) / close) * 100;
            }

            return arefClose > 0 && close > 0 ? roc : -10000.0;
        } catch (Exception e) { // If date is not found
            System.out.println(
                    "WARNING: ROC didnt found the close: " + date
                            + " length: " + length);
            return 0;
        }
    }

}
