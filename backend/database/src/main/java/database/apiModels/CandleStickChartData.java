package database.apiModels;

import java.util.List;
import database.TickData;

/*
 * Not using HashArrays, but instead using a list of TickData objects.
 */

public class CandleStickChartData {

    public String ticker;
    public String name;
    public String listed;
    public List<TickData> ticksSorted;

}
