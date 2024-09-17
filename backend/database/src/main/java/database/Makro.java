package database;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import utils.HashArray;

@Getter
@Builder(builderMethodName = "makroBuilder")
public class Makro implements DateValueRetriever {

    private final String type;
    private final String frequence;
    private final HashArray<Integer, Double> ticks;

    @Override
    public String toString() {
        return "Makro{type='" + type + '\'' + ", frequence='"
                + frequence + '\'' + '}';
    }

    @Override
    public double getClose(int date) { return ticks.get(date); }

    @Override
    public double getAref(int date, int lookaback) {
        return ticks.getAref(date, lookaback);
    }

    @Override
    public List<Integer> getDateList() {
        return ticks.getKeyList();
    }

    @Override

    public int getClosestDate(int date) {
        return ticks.getClosestKey(date);
    }

}
