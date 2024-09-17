/**
 * This class represents the data for a specific stock. It is used as a
 * container for the data accessed from the database.
 */

package database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import lombok.Builder;
import lombok.Getter;

import utils.HashArray;

@Getter
public class Symbol implements DateValueRetriever {

    private final int id;
    private final String name;
    private final String ticker;
    private final String listed;
    private HashArray<Integer, TickData> tickDatas = new HashArray<>();
    private final MarketPlace marketPlace;
    private HashArray<Integer, Dividend> dividends = new HashArray<>();

    @Builder(builderMethodName = "symbolBuilder")
    public Symbol(int id, String name, String ticker,
            String listed, List<TickData> tickDataList,
            MarketPlace marketPlace,
            List<Dividend> dividendsList) {
        if (name == null) {
            throw new IllegalArgumentException(
                    "Missing name in symbol!");
        }
        this.id = id;
        this.name = name;
        this.ticker = ticker;
        this.listed = listed; // .intern()
        this.marketPlace = marketPlace;
        setTickData(tickDataList);
        setDividends(dividendsList);
    }

    private void setTickData(List<TickData> tickData) {
        this.tickDatas = new HashArray<>();
        tickData.sort((o1, o2) -> o1.getDate() - o2.getDate());

        for (TickData tick : tickData) {
            this.tickDatas.put(tick.getDate(), tick);
        }
    }

    private void setDividends(List<Dividend> dividendsList) {
        this.dividends = new HashArray<>();
        dividendsList
                .sort((o1, o2) -> o1.getDate() - o2.getDate());

        for (Dividend dividend : dividendsList) {
            this.dividends.put(dividend.getDate(), dividend);
        }
    }

    public double getClose(int date) {
        return getValue(date, TickData::getClose);
    }

    public double getLow(int date) {
        return getValue(date, TickData::getLow);
    }

    public double getHigh(int date) {
        return getValue(date, TickData::getHigh);
    }

    public double getOpen(int date) {
        return getValue(date, TickData::getOpen);
    }

    public double getVolume(int date) {
        return getValue(date, TickData::getVolume);
    }

    @Override
    public double getAref(int date, int lookaback) {
        return getArefClose(date, lookaback);
    }

    public double getArefClose(int date, int lookback) {
        return getArefValue(date, lookback,
                (data, lb) -> data.getClose());
    }

    public double getArefLow(int date, int lookback) {
        return getArefValue(date, lookback,
                (data, lb) -> data.getLow());
    }

    public double getArefHigh(int date, int lookback) {
        return getArefValue(date, lookback,
                (data, lb) -> data.getHigh());
    }

    public double getArefOpen(int date, int lookback) {
        return getArefValue(date, lookback,
                (data, lb) -> data.getOpen());
    }

    public double getArefVolume(int date, int lookback) {
        return getArefValue(date, lookback,
                (data, lb) -> data.getVolume());
    }

    public boolean hasData(int date, int loockback) {
        return tickDatas.hasData(date, loockback);
    }

    public boolean hasData(int date) {
        return tickDatas.hasData(date);
    }

    // TODO: use this is position to calculate the price, if market isnt open, use
    // market previus open
    public boolean isTradingDayForMarket(int date) {
        return marketPlace.hasDate(date);
    }

    @Override
    public int getClosestDate(int date) {
        return tickDatas.getClosestKey(date);
    }

    @Override
    public List<Integer> getDateList() {
        return tickDatas.getKeyList();
    }

    private double getValue(int date,
            Function<TickData, Double> valueRetriever) {
        try {
            return valueRetriever.apply(tickDatas.get(date));
        } catch (Exception e) {
            System.out.println("WARNING: The value for '" + date
                    + "' is not in THE database. Symbol: "
                    + name);
            return -1;
        }
    }

    private double getArefValue(int date, int lookback,
            BiFunction<TickData, Integer, Double> valueRetriever) {
        try {
            return valueRetriever.apply(
                    tickDatas.getAref(date, lookback), lookback);
        } catch (Exception e) {
            System.out.println("WARNING: The aref value for '"
                    + date + "' " + " and lookback: " + lookback
                    + " is not in THE database. Symbol: "
                    + name);
            return -1;
        }
    }

    public String getCurrency() {
        return marketPlace.getCurrency();
    }

    public int getArefDate(int date, int i) {
        return tickDatas.getArefKey(date, i);
    }

    public List<Integer> getDividendXDates() {
        return dividends.getKeyList();
    }

    public int getClosestDividendDate(int date) {
        return dividends.getClosestKey(date);
    }

    /*
     * Only latest divident divided by the price
     */
    public double getDirectReturn(int date) {
        try {
            if (tickDatas.size() == 0 || dividends.size() == 0) {
                return -1;
            }

            int dateOneYearAgo = date - 10000;
            List<Dividend> dividendsPastYear = new ArrayList<>();

            int latestDividendDate = getClosestDividendDate(
                    date);
            if (latestDividendDate == -1) {
                return -1;
            }
            for (int i = 0; i < 12; i++) {
                Dividend dividend;
                try {
                    dividend = dividends
                            .getAref(latestDividendDate, i);
                } catch (Exception e) {
                    break; // No more dividends
                }
                if (dividend.getDate() > dateOneYearAgo) {
                    dividendsPastYear.add(dividend);
                } else {
                    break;
                }
            }
            if (dividendsPastYear.isEmpty()) {
                return -1;
            }

            double dividendSum = dividendsPastYear.stream()
                    .mapToDouble(Dividend::getValue).sum();
            double price = getClose(getClosestDate(date));
            double directReturn = 100 * dividendSum / price;
            return directReturn;
        } catch (Exception e) {
            System.out.println(
                    "WARNING: Has not enough data to calculate direct return. Date: '"
                            + date
                            + "' is not in THE database. Symbol: "
                            + name);
            return -1;
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": name: " + name;
    }

    public static void main(String[] args) {
        Symbol eqnr = new Symbol.SymbolBuilder().id(1)
                .name("equinor").ticker("eqnr")
                .listed("Oslo Børs")
                .tickDataList(Arrays.asList()).marketPlace(null)
                .build();

        System.out.println(eqnr.getName());
        System.out.println(eqnr.getListed());
    }

}
