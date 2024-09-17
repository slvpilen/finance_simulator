package indicators;

import database.DateValueRetriever;
import simulator.bases.IndicatorBase;

public class BollingerBands extends IndicatorBase {

    private int length;
    private double width;
    private MovingAverage mov;
    private StandardDeviation std;

    public BollingerBands(DateValueRetriever dateValueRetriever,
            int length, double width) {
        super(dateValueRetriever);
        this.length = length;
        this.width = width;
        this.mov = new MovingAverage(dateValueRetriever, length);
        this.std = new StandardDeviation(dateValueRetriever,
                length, width);
    }

    @Override
    public double getValue(int date) {
        throw new UnsupportedOperationException(
                "Not supported, use getUpper, getLower or getMiddle");
    }

    public double getUpper(int date) {
        return getValue(date, true);
    }

    public double getLower(int date) {
        return getValue(date, false);
    }

    public double getValue(int date, boolean isUpper) {

        if (isUpper) {
            return mov.getValue(date)
                    + width * std.getValue(date);
        } else {
            return mov.getValue(date)
                    - width * std.getValue(date);
        }
    }

    public double getMiddle(int date) {
        return mov.getValue(date);
    }

}
