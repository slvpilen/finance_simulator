package indicators;

import database.DateValueRetriever;
import simulator.bases.IndicatorBase;
import lombok.Setter;

public class StandardDeviation extends IndicatorBase {

    private int length;
    @Setter
    private double width;
    private MovingAverage mov;

    public StandardDeviation(
            DateValueRetriever dateValueRetriever, int length,
            double width) {
        super(dateValueRetriever);
        this.length = length;
        this.width = width;
        this.mov = new MovingAverage(dateValueRetriever, length);
    }

    @Override
    public double getValue(int date) {
        return getValue(date, this.length, this.width);

    }

    public double getValue(int date, double width) {
        return getValue(date, this.length, width);

    }

    public double getValue(int date, int length) {

        return getValue(date, length, this.width);
    }

    public double getValue(int date, int length, double width) {
        mov.setLength(length);
        double xBar = mov.getValue(date);

        double variance = 0;
        for (int i = 0; i < length; i++) {
            variance += Math.pow(
                    (dateValueRetriever.getAref(date, i) - xBar),
                    2);
        }
        variance /= (length - 1);

        double stdev = Math.sqrt(variance);

        return width * stdev;

    }

}
