/**
*
*A class representing a closed trade, which includes the buy and sell price, the last amount sold, and
*the dates of all buys and sells associated with the position.
* @author Oskar
* @version 1.0
* @since 2023-02-01
*/

package simulator.analyse;

import java.util.List;
import database.Symbol;
import database.TradeEvent;
import lombok.Getter;

@Getter
public class ClosedTrade {
    private Symbol symbol;
    private double buyPrice;
    private double sellPrice;
    private int lastAmount;
    private List<TradeEvent> tradeEvents;

    /**
     * Creates a new ClosedTrade object.
     * 
     * @param position   The Position object that the trade is based on.
     * @param lastAmount The lastAmount of the position that was exchanged.
     * 
     * @throws IllegalArgumentException if the position is flipped from long to
     *                                  short or short to long without making a new
     *                                  position.
     */
    public ClosedTrade(Position position, int lastAmount) {
        flippingPositionCheck(position, lastAmount);

        boolean longTrade = lastAmount < 0;

        this.lastAmount = lastAmount;
        this.tradeEvents = position.getTradeEvents();

        if (longTrade) {
            this.buyPrice = position.getAveragePrice();
            this.sellPrice = position.getLastSellPrice();
        } else { // shortTrade
            this.buyPrice = position.getLastBuyPrice();
            this.sellPrice = position.getAveragePrice();
        }
        this.symbol = position.getSymbol();
    }

    /**
     * Returns the list of buy and sell dates associated with the position.
     * 
     * @return the list of buy and sell dates associated with the position.
     */
    public List<TradeEvent> getBuyAndSellDates() {
        return tradeEvents;
    }

    /**
     * Checks if the position is being flipped from long to short or short to long
     * without making a new position.
     * 
     * @param position The Position object to check.
     * @param amount   The amount of the position that was sold.
     * 
     * @return true if the position is not being flipped; otherwise false.
     * 
     * @throws IllegalArgumentException if the position is flipped from long to
     *                                  short or short to long without making a new
     *                                  position.
     */
    private boolean flippingPositionCheck(Position position,
            int sellAmount) {
        if ((position.getHoldings() < 0 && sellAmount < 0)
                || (position.getHoldings() > 0
                        && sellAmount > 0)) {
            throw new IllegalArgumentException(
                    "Can not flip from long to short or short to long without making a new position!");
        }
        return true;
    }

    /**
     * 
     * @return the percentage increase from the buy price to the sell price.
     */
    public double getProsentIncreas() {
        return ((sellPrice - buyPrice) / buyPrice) * 100;
    }

    /**
     * If the closing amount is negative, the order is a long order.
     * 
     * @return the percentage increase from the buy price to the sell price.
     */
    public boolean isLongTrade() { return lastAmount < 0; }

    /**
     * 
     * @return the earnings in the local currency
     */
    public double getEarning() {
        return (sellPrice - buyPrice) * lastAmount;
    }

    public int getLastAmount() { return this.lastAmount; }
}
