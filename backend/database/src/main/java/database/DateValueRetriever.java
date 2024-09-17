package database;

import java.util.List;

/*
 * NOTE: Not sure where to store this. It is used in indicator and implemented by Symbol and ResultService
 */

public interface DateValueRetriever {

    public double getClose(int date);

    public double getAref(int date, int lookaback);

    public List<Integer> getDateList();

    public int getClosestDate(int date);
}
