package simulator.result;

import java.util.List;

import database.TradeEvent;
import utils.HashArray;

public interface ResultReadable {

    public HashArray<Integer, Double> getdailyAccountValues();

    public HashArray<Integer, Double> getdailyAccountExponation();

    public double getStatsIncreasDTrades();

    public double getStatsHitrate();

    public int getNumberOfClosedTrades();

    public double getReturn();

    public double getMaxDrawdown();

    public double getReturn(int date, int loockbackPeriod);

    public double getDrawdown(Integer date);

    public double getClose(int date);

    public double getAref(int date, int lookaback);

    public List<Integer> getDateList();

    public List<TradeEvent> getTradeEvents();

    public double getStatsIncreasDTradesLongTrades();

    public double getStatsIncreasDTradesShortTrades();
}
