package simulator.analyse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import database.MarketPlace;
import database.Symbol;
import database.TickData;
import database.TradingHours;
import simulator.bases.StrategyBase;
import utils.HashArray;

public class AccountTest {

    StrategyBase strategy;
    Account account;

    @BeforeEach
    void setup() {

        List<TickData> ticks = Arrays.asList(new TickData(
                20240107, 88, 202, 78, 200, 200, 999000));

        TradingHours tradingHours = new TradingHours(900, 1530);
        HashArray<Integer, TradingHours> dateTradingHours = new HashArray<>();
        ticks.forEach(tick -> dateTradingHours
                .put(tick.getDate(), tradingHours));
        MarketPlace marketPlace = new MarketPlace("Oslo Børs",
                "Norway", "Oslo", "NOK", "CET",
                dateTradingHours);

        Symbol eqnr = new Symbol(60, "Dummy_stock", "DMS",
                "Lom Børs", ticks, marketPlace, null);

        strategy = new DummyStrategy(eqnr, 100000);
        account = new Account(100000);
        account.addStrategy(strategy);
    }

    @Test
    @DisplayName("Negative cash not allowed")
    void testConstructurInputs() {
        assertThrows(IllegalArgumentException.class,
                () -> new Account(-10), "negative cash!");
    }

    @Test
    @DisplayName("Account value unchanged when buying")
    void testAddingPostitionAccountValue() {
        account.addPosition(strategy, 10, 100);
        assertEquals(100000, account.getAccountValue(),
                "wrong account value");
    }

    @Test
    @DisplayName("Holdings increase when buying")
    void testAddingPostitionAmount() {
        account.addPosition(strategy, 10, 100);
        assertEquals(10, strategy.getInnehav(), "wrong amount");
    }

    @Test
    @DisplayName("Last buy price update when buying")
    void testAddingPostitionLastBuyPrice() {
        account.addPosition(strategy, 10, 100);
        assertEquals(100, strategy.getLastBuyPrice(),
                "wrong price");
    }

    @Test
    @DisplayName("Cash decrease when buying")
    void testAddingPostitionCash() {
        account.addPosition(strategy, 10, 100);
        account.addPosition(strategy, 10, 100);
        assertEquals(98000, account.getCash(), "wrong cash");
    }

    @Test
    @DisplayName("Gives correct closedTrades")
    void testAddingPostitionClosedTrades() {
        account.addPosition(strategy, 10, 100);
        account.addPosition(strategy, 10, 100);
        assertEquals(-1, account.resultService.getStatsHitrate(),
                "should be none closed trades");
    }

    @Test
    @DisplayName("Gives correct closedTrades")
    void testClosingPostition5() {

    }

    @Test
    @DisplayName("Hitrate correct when closing postition")
    void testClosingPostitionHitrate() {
        account.addPosition(strategy, 10, 100);
        if (account.getCash() < 100000) {
            // Programmatically trigger the debugger when the condition is met
            System.out.println(
                    "Debugging condition met: account.getCash() < 100000");
        }
        account.addPosition(strategy, -5, 105);
        assertEquals(1, account.resultService.getStatsHitrate(),
                "one closed trade with increase");
        account.addPosition(strategy, -3, 99);
        assertEquals(0.5,
                account.resultService.getStatsHitrate(),
                "two trade one win one loose");
        account.addPosition(strategy, -1, 99);
        account.addPosition(strategy, -1, 99);
        assertEquals(0.25,
                account.resultService.getStatsHitrate(),
                "foure trade one win tree loose");

    }

    @Test
    @DisplayName("Shorting not allowed in long only class")
    void testIllegalShorting() {
        DummyStrategy strategtLongOnly = (DummyStrategy) strategy;
        strategtLongOnly.setLongAllowed(true);
        strategtLongOnly.setShortAllowed(false);
        account.addPosition(strategtLongOnly, 10, 100);
        account.addPosition(strategtLongOnly, -5, 105);
        System.out.println(strategtLongOnly.getInnehav());
        assertThrows(IllegalStateException.class, () -> {
            account.addPosition(strategtLongOnly, -6, 99);
        }, "Negative innehav på konto bør gi Illegalargumentexception, så lenge det kun er for long");
        // account.addPosition(strategy, -6, 105);
    }

    // shorting allowed in when LONG_ALLOWED and SHORT_ALLOWED is false, account
    // handle
    // flipping from long to short<->
    @Test
    @DisplayName("Flipping from long to short")
    void testLegalShorting() {
        DummyStrategy strategtLongAndShort = (DummyStrategy) strategy;
        strategtLongAndShort.setLongAllowed(true);
        strategtLongAndShort.setShortAllowed(true);
        account.addPosition(strategtLongAndShort, 10, 100);
        account.addPosition(strategtLongAndShort, -20, 105);
        assertEquals(1, account.resultService.getStatsHitrate(),
                "account handle flipping from long to short");
        assertEquals(100,
                strategtLongAndShort.getOpenPosition()
                        .getLastBuyPrice(),
                "Should be latest prce of the last positive amount");
        assertEquals(105,
                strategtLongAndShort.getOpenPosition()
                        .getLastSellPrice(),
                "Should be the price of the last sell");
        account.addPosition(strategtLongAndShort, -10, 110);
        assertEquals(107.5,
                strategtLongAndShort.getOpenPosition()
                        .getAveragePrice(),
                "avg-price after flipping");
    }

    // shorting allowed in when LONG_ALLOWED and SHORT_ALLOWED is false, account
    // handle
    // flipping from long to short<->
    @Test
    @DisplayName("Flipping from short to long")
    void testLegalShorting2() {
        DummyStrategy strategtLongAndShort = (DummyStrategy) strategy;
        strategtLongAndShort.setLongAllowed(true);
        strategtLongAndShort.setShortAllowed(true);
        account.addPosition(strategtLongAndShort, -10, 100);
        account.addPosition(strategtLongAndShort, 20, 105);
        assertEquals(0, account.resultService.getStatsHitrate(),
                "account handle flipping from long to short");
        assertEquals(105,
                strategtLongAndShort.getOpenPosition()
                        .getLastBuyPrice(),
                "Shold be the price of the last buy");
        assertEquals(100,
                strategtLongAndShort.getOpenPosition()
                        .getLastSellPrice(),
                "Should be the price of the last sell");
        account.addPosition(strategtLongAndShort, 10, 110);
        assertEquals(107.5,
                strategtLongAndShort.getOpenPosition()
                        .getAveragePrice(),
                "avg-price after flipping");
    }

    @Test
    @DisplayName("Cash on account")
    void testLegalShortingCash() {
        DummyStrategy strategtLongAndShort = (DummyStrategy) strategy;
        strategtLongAndShort.setLongAllowed(true);
        strategtLongAndShort.setShortAllowed(true);
        account.addPosition(strategtLongAndShort, -10, 1000);
        assertEquals(110000, account.getCash(),
                "Cash increas when shorting");
    }

}
