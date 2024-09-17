/**
* This class represents a position in a financial simulation, specifically one symbolName (i.e., security or asset).
* It keeps track of the current holdings, average buy price, last sell price, last positive and negative changes in holdings,
* a list of all buy and sell trades with their dates, the strategy used to analyze the symbolName, and the current date and cash in the simulation.
* It also provides methods to add a new position, get the list of all buy and sell trades, calculate the entry value and current value of the position,
* check if there is any position currently held, and get the symbolName, holdings, average buy price, last sell price, last positive and negative changes in holdings,
* and the date and cash in the simulation.
*
* @author Oskar
* @version 1.0
* @since 2023-02-01
*/

package simulator.analyse;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

import database.Symbol;
import database.TradeEvent;
import database.TradeEvent.TradeType;
import simulator.bases.StrategyBase;

@Getter
public class Position implements AnalyseListener {

    private Symbol symbol;
    protected int holdings;
    private double averagePrice;
    private double lastBuyPrice, lastSellPrice;
    private int lastBuy, lastSell; // The date of the last buy day, sell
    private List<TradeEvent> tradeEvents; // A list of all buy and sell trades with their dates
    private StrategyBase strategy; // The strategy used to analyze the symbolName
    private int currentSimulationDate; // The current date in the simulation, updated with observed/observeator

    /**
     * Constructs a new Position object with the given symbolName, amount, price and
     * strategy.
     * 
     * @param symbolName The symbolName
     * @param amount     The number of holdings
     * @param price      The average buy price
     * @param strategy   The strategy used to analyze the symbolName
     * @throws IllegalArgumentException If the amount or price is not positive
     */
    public Position(Symbol symbol, int amount, double price,
            StrategyBase strategy, int date) {
        this.symbol = symbol;
        validate(amount, price);

        this.averagePrice = price;
        this.holdings = amount;
        this.currentSimulationDate = date;
        this.strategy = strategy;
        this.tradeEvents = new ArrayList<>();

        boolean isSelling = amount < 0;
        if (isSelling) {
            this.lastSell = date;
            this.lastSellPrice = price;
            addSellingDateToList();
        } else {
            this.lastBuy = date;
            this.lastBuyPrice = price;
            addBuyingDateToList();
        }
    }

    /**
     * Adds a new position with the given amount and price to the current holdings
     * of the position.
     * 
     * @param amount The number of holdings to add (positive for buy, negative for
     *               sell)
     * @param price  The price of the transaction
     * @throws IllegalArgumentException If the amount is zero or the price is not
     *                                  positive
     */
    public void addPosition(int amount, double price) {
        validate(amount, price);

        if (amount > 0) {
            handleBuying(amount, price);
        } else {
            handleSelling(amount, price);
        }
    }

    protected void handleBuying(int amount, double price) {
        boolean isLongOrder = holdings >= 0 && amount > 0;
        boolean isCoverOrder = holdings < 0 && amount > 0;
        boolean isFlippingFromShortToLong = holdings < 0
                && amount > 0 && holdings + amount > 0;

        lastBuy = currentSimulationDate;
        lastBuyPrice = price;
        addBuyingDateToList();

        if (isLongOrder) {
            double oldHoldings = holdings;
            averagePrice = (averagePrice * oldHoldings
                    + price * amount) / (amount + oldHoldings);
            holdings += amount;
        } else if (isCoverOrder && !isFlippingFromShortToLong) {
            holdings += amount;
        } else if (isFlippingFromShortToLong) {
            throw new IllegalStateException(
                    "Can't flip from short to long without creating new position in Account!");
        } else {
            throw new IllegalStateException(
                    "Not legal state for handling buying! Should not be here");
        }
    }

    protected void handleSelling(int amount, double price) {
        boolean shortOrder = holdings <= 0 && amount < 0;
        boolean sellOrder = holdings > 0 && amount < 0;
        boolean flippingFromLongToShort = holdings > 0
                && amount < 0 && holdings + amount < 0;

        lastSell = currentSimulationDate;
        lastSellPrice = price;
        addSellingDateToList();

        if (shortOrder) {
            double oldHoldings = holdings;
            averagePrice = (averagePrice * Math.abs(oldHoldings)
                    + price * Math.abs(amount))
                    / Math.abs((amount + oldHoldings));
            holdings += amount;
        } else if (sellOrder && !flippingFromLongToShort) {
            holdings += amount;
        } else if (flippingFromLongToShort) {
            throw new IllegalShortException(
                    "Can't flip from long to short without creating new position in Account!");
        } else {
            throw new IllegalStateException(
                    "Not legal state for handling selling! Should not be here");
        }
    }

    public double getEntryValue() {
        return averagePrice * holdings;
    }

    /**
     * Handling current price == -1 by using avg price instead. If missing data, it
     * the current price will be -1. This need to be handled better
     * 
     * @return Value of the current marked value of the position
     */
    public double getPositionValue() {
        Symbol symbol = strategy.getSymbol();
        boolean hasData = symbol.hasData(currentSimulationDate);
        boolean isTradingDayForMarket = symbol
                .isTradingDayForMarket(currentSimulationDate);

        if (!isTradingDayForMarket) {
            return calculateValueForNonTradingDay(symbol);
        }

        if (!hasData) {
            logWarningForNoData();
            return holdings * averagePrice;
        }

        double currentPrice = strategy.getCurrentPrice();
        validateCurrentPrice(currentPrice);

        return holdings * currentPrice;
    }

    private double calculateValueForNonTradingDay(
            Symbol symbol) {
        int nearestSymbolDate = symbol
                .getClosestDate(currentSimulationDate);

        if (nearestSymbolDate == -1) {
            logWarningForNoNearestDate();
            return holdings * averagePrice;
        }

        return holdings * symbol.getClose(nearestSymbolDate);
    }

    private void logWarningForNoData() {
        System.out.println("WARNING! No data for date: "
                + currentSimulationDate + " in "
                + symbol.getName()
                + " but it is a trading day for market. Average entry price is used instead in date's position value");
    }

    private void logWarningForNoNearestDate() {
        System.out.println("WARNING! No nearest date: "
                + currentSimulationDate + " in "
                + symbol.getName()
                + " and no nearest date found. Latest symbol date is used instead in date's position value");
    }

    private void validateCurrentPrice(double currentPrice) {
        if (currentPrice == -1) {
            throw new IllegalStateException(
                    "Current price is -1, but has checked for data. Should not be possible");
        }
    }

    public boolean hasPosition() {
        return holdings > 0 || holdings < 0;
    }

    private void addBuyingDateToList() {
        tradeEvents.add(new TradeEvent(TradeType.BUY,
                currentSimulationDate, symbol));
    }

    private void addSellingDateToList() {
        tradeEvents.add(new TradeEvent(TradeType.SELL,
                currentSimulationDate, symbol));
    }

    private void validate(int amount, double price) {
        if (amount == 0) {
            throw new IllegalArgumentException(
                    "Trying to create a positon with amount: 0");
        }

        boolean isPositivePrice = price > 0;
        if (!isPositivePrice) {
            throw new IllegalArgumentException(
                    "Price on symbol " + symbol.getName());
        }
    }

    @Override
    public String toString() {
        return symbol.getName() + " (" + "holdings: " + holdings
                + ")";

    }

    @Override
    public void iterationDate(int date, double cash) {
        this.currentSimulationDate = date;
    }

    public class IllegalShortException
            extends IllegalStateException {
        public IllegalShortException(String message) {
            super(message);
        }
    }

}
