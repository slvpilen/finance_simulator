/**
* Indicators can be made by extend this. Indicators can be used on Symbol and ResultService. 
* That means it's possible to use it inside a strategy on Symbol, but also on a simulation result.
* 
* @author Oskar
* @version 1.0
* @since 2024-01-03
*/

package simulator.bases;

import database.DateValueRetriever;
import simulator.analyse.AnalyseListener;

public abstract class IndicatorBase
        implements Indicator, AnalyseListener {

    protected DateValueRetriever dateValueRetriever;
    protected int date;

    public IndicatorBase(DateValueRetriever dateValueRetriever) {
        this.dateValueRetriever = dateValueRetriever;
    }

    public double getValue() {
        if (date == 0) {
            throw new IllegalArgumentException("Date not set.");
        }
        return getValue(date);
    }

    public abstract double getValue(int date);

    public void iterationDate(int date, double cash) {
        this.date = date;
    }

}
