/**
 * This class provides a service for analyzing the results of a trading simulation. It keeps track of closed trades,
 * open positions, daily account values, and various statistics.
 */

package simulator.result;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import database.DateValueRetriever;
import database.TradeEvent;
import simulator.analyse.Position;
import simulator.analyse.AnalyseListener;
import simulator.analyse.ClosedTrade;
import utils.HashArray;

public class ResultService implements AnalyseListener,
        ResultReadable, DateValueRetriever {

    private int date; // The current date; rename it?
    private double cash; // The current amount of cash

    private List<ClosedTrade> closedTrades = new ArrayList<>();
    private List<Position> openPositions = new ArrayList<>();

    private HashArray<Integer, Double> dailyAccountValues = new HashArray<>();
    private HashArray<Integer, Double> dailyAccountExponations = new HashArray<>();

    private HashArray<Integer, Double> drawdownList = new HashArray<>();
    private boolean drawdownListUpdated = false;

    public List<TradeEvent> getTradeEvents() {
        List<TradeEvent> closedTradeEvents = closedTrades
                .stream()
                .flatMap(
                        trade -> trade.getTradeEvents().stream())
                .collect(Collectors.toList());

        List<TradeEvent> openPositionEvents = openPositions
                .stream()
                .flatMap(
                        trade -> trade.getTradeEvents().stream())
                .collect(Collectors.toList());

        List<TradeEvent> allTradeEvents = new ArrayList<>();
        allTradeEvents.addAll(closedTradeEvents);
        allTradeEvents.addAll(openPositionEvents);

        return allTradeEvents;

    }

    /**
     * Adds a closed trade to the list of closed trades. Assuming not flipping from
     * long to short or short to long p.getHolding is updated with amount!
     *
     * @param p      The position associated with the closed trade.
     * @param amount The amount of the trade.
     */
    public void addClosedTrade(Position position, int amount) {
        boolean isSellingLongOrder = position.getHoldings() >= 0
                && amount < 0;
        boolean isCoverShortOrder = position.getHoldings() <= 0
                && amount > 0;

        if (isSellingLongOrder || isCoverShortOrder) {
            ClosedTrade newClosedTrade = new ClosedTrade(
                    position, amount);
            closedTrades.add(newClosedTrade);
        }
        drawdownListUpdated = false;
    }

    /**
     * Adds the account value for the current day to the list of daily account
     * values.
     */
    public void addDailyAccountValue() {
        double innehaveValue = openPositions.stream()
                .mapToDouble(
                        position -> position.getPositionValue())
                .sum();

        double accountValue = innehaveValue + cash;
        double accountExponation = innehaveValue / accountValue;

        dailyAccountValues.put(date, accountValue);
        dailyAccountExponations.put(date, accountExponation);

        drawdownListUpdated = false;
    }

    /**
     * Returns the percentage increase of the last closed trade.
     *
     * @return The percentage increase of the last closed trade, or null if there
     *         are no closed trades.
     */
    public Double getLastTradeIncrease() {
        if (closedTrades == null || closedTrades.size() == 0)
            return null;

        return closedTrades.get(closedTrades.size() - 1)
                .getProsentIncreas();
    }

    public void remove(Position p) { openPositions.remove(p); }

    /**
     * Returns the hit rate of closed trades, which is the percentage of closed
     * trades that resulted in a profit.
     * 
     * @return the hit rate of closed trades
     * @return if ther is no closed trades -1 will be returned
     */
    @Override
    public double getStatsHitrate() {
        if (closedTrades.isEmpty()) {
            return -1;
        }

        return closedTrades.stream().mapToDouble(
                trade -> trade.getProsentIncreas() > 0.0 ? 1.0
                        : 0.0)
                .sum() / closedTrades.size();
    }

    /**
     * Calculates the maximum drawdown of the account value.
     *
     * @return the maximum drawdown as a positive number
     */
    @Override
    public double getMaxDrawdown() {
        double allTimeHighe = dailyAccountValues
                .getValueAtIndex(0);
        double maxDD = -0.0; // negative number

        for (Double accountValue : dailyAccountValues) {
            double drawdown = 100 * (accountValue - allTimeHighe)
                    / allTimeHighe;
            if (drawdown < maxDD)
                maxDD = drawdown;

            if (accountValue > allTimeHighe)
                allTimeHighe = accountValue;
        }
        return Math.abs(maxDD); // return positive number
    }

    public HashArray<Integer, Double> getDrawdownList() {
        if (drawdownList == null || !drawdownListUpdated)
            updateDrawdownList();

        drawdownListUpdated = true;

        return new HashArray<>(drawdownList);
    }

    @Override
    public double getDrawdown(Integer date) {
        return getDrawdownList().get(date);
    }

    private void updateDrawdownList() {
        drawdownList.clear();

        double allTimeHighe = dailyAccountValues
                .getValueAtIndex(0);
        for (double accountValue : dailyAccountValues) {
            double drawdown = 100 * (accountValue - allTimeHighe)
                    / allTimeHighe;
            drawdown = Math.min(drawdown, 0);
            drawdownList.put(date, drawdown);

            if (accountValue > allTimeHighe) {
                allTimeHighe = accountValue;
            }
        }
    }

    // TODO: move to a own class like Indicator
    /**
     * Calculates the Calmar ratio of the account historical values.
     *
     * @return the Calmar ratio
     */
    public double getCalmarRatio() {
        double maxDD = getMaxDrawdown();
        if (maxDD <= 0.0)
            maxDD = 1.0;

        return getReturn() / maxDD;
    }

    /**
     * Calculates the average percentage increase of closed trades.
     *
     * @return the average percentage increase
     */
    @Override
    public double getStatsIncreasDTrades() {
        return closedTrades.stream()
                .mapToDouble(trade -> trade.getProsentIncreas())
                .sum() / getNumberOfClosedTrades();
    }

    /**
     * Calculates the average percentage increase of long trades.
     *
     * @return the average percentage increase long trades
     */
    public double getStatsIncreasDTradesLongTrades() {
        return closedTrades.stream()
                .filter(trade -> trade.isLongTrade())
                .mapToDouble(trade -> trade.getProsentIncreas())
                .sum() / getNumberOfClosedTradesLongTrades();
    }

    /**
     * Calculates the average percentage increase of long trades.
     *
     * @return the average percentage increase long trades
     */
    public double getStatsIncreasDTradesShortTrades() {
        return closedTrades.stream()
                .filter(trade -> !trade.isLongTrade())
                .mapToDouble(trade -> trade.getProsentIncreas())
                .sum() / getNumberOfClosedTradesShortTrades();
    }

    @Override
    public int getNumberOfClosedTrades() {
        return closedTrades.size();
    }

    private int getNumberOfClosedTradesLongTrades() {
        return (int) closedTrades.stream()
                .filter(trade -> trade.isLongTrade()).count();
    }

    private int getNumberOfClosedTradesShortTrades() {
        return (int) closedTrades.stream()
                .filter(trade -> !trade.isLongTrade()).count();
    }

    /**
     * Gets the list of daily account values.
     *
     * @return the list of daily account values
     */
    @Override
    public HashArray<Integer, Double> getdailyAccountValues() {
        return new HashArray<>(dailyAccountValues);
    }

    /**
     * Gets the list of daily account exponation.
     *
     * @return the list of daily account exop
     */
    @Override
    public HashArray<Integer, Double> getdailyAccountExponation() {
        return new HashArray<>(dailyAccountExponations);
    }

    /**
     * Calculates the total return of the account value as a percentage.
     *
     * @return the total return as a percentage
     * @throws IllegalArgumentException if no daily values are stored
     */
    @Override
    public double getReturn() {
        if (dailyAccountValues.isEmpty()) {
            System.out.println(dailyAccountValues.size());
            throw new IllegalArgumentException(
                    "No daily value stored.");
        }
        return (dailyAccountValues
                .getValueAtIndex(dailyAccountValues.size() - 1)
                - dailyAccountValues.getValueAtIndex(0))
                / dailyAccountValues.getValueAtIndex(0) * 100;
    }

    @Override
    public double getReturn(int date, int loockback) {
        if (dailyAccountValues.isEmpty())
            throw new IllegalArgumentException(
                    "No daily value stored.");

        // TODO: This should not happen, so trow exception?
        if (!dailyAccountValues.hasData(date))
            System.out.println("Could't find date: " + date
                    + " in dailyAccountValues");

        if (!dailyAccountValues.hasData(date, loockback))
            System.out.println("Could't find date: lookback: "
                    + loockback + " in dailyAccountValues");

        double dateValue = getClose(date);

        double loockbackValue = dailyAccountValues.getAref(date,
                loockback);

        return (dateValue - loockbackValue) / loockbackValue
                * 100;
    }

    @Override
    public void iterationDate(int date, double cash) {
        this.date = date;
        this.cash = cash;
    }

    @Override
    public void addOpenPosition(Position positon) {
        openPositions.add(positon);
    }

    @Override
    public void removeOpenPosition(Position positon) {
        openPositions.remove(positon);
    }

    @Override
    public double getClose(int date) {
        return dailyAccountValues.get(date);
    }

    @Override
    public double getAref(int date, int lookback) {
        Double aref = dailyAccountValues.getAref(date, lookback);
        if (aref == null) {
            System.out.println("WARNING: The close for '" + date
                    + "' and lookback: " + lookback
                    + " is not in the resultservice");
            return -1;
        }
        return aref;
    }

    @Override
    public List<Integer> getDateList() {
        return dailyAccountValues.getKeyList();
    }

    @Override
    public int getClosestDate(int date) {
        return dailyAccountValues.getClosestKey(date);
    }
}