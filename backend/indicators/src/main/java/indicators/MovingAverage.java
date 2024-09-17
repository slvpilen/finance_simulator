package indicators;

import database.DateValueRetriever;
import lombok.Setter;
import simulator.bases.IndicatorBase;

public class MovingAverage extends IndicatorBase {

    @Setter
    private int length;

    public MovingAverage(DateValueRetriever dateValueRetriever,
            int length) {
        super(dateValueRetriever);
        this.length = length;
    }

    @Override
    public double getValue(int date) {
        double sum = 0;
        for (int i = 0; i < length; i++) {
            sum += dateValueRetriever.getAref(date, i);
        }
        double average = sum / length;
        return average;
    }

}
