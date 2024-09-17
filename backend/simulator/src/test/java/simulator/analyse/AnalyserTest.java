package simulator.analyse;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import database.MarketPlace;
import database.Symbol;
import utils.HashArray;
import database.TickData;
import database.TradingHours;

public class AnalyserTest {

    Symbol symbol;
    TestStrategy testStrategy;
    Analyser analyser;

    @BeforeEach
    void setup() {
        TickData tick1 = new TickData(20240102, 100, 102, 98,
                101, 101, 95000);
        TickData tick2 = new TickData(20240103, 105, 108, 101,
                103, 103, 94000);
        TickData tick3 = new TickData(20240104, 95, 100, 90, 98,
                98, 95000);
        TickData tick4 = new TickData(20240105, 90, 94, 80, 90,
                90, 94000);
        TickData tick5 = new TickData(20240106, 88, 92, 78, 88,
                88, 95000);
        TickData tick6 = new TickData(20240107, 88, 202, 78, 200,
                200, 999000);

        List<TickData> ticks = Arrays.asList(tick1, tick2, tick3,
                tick4, tick5, tick6);

        TradingHours tradingHours = new TradingHours(900, 1530);
        HashArray<Integer, TradingHours> dateTradingHours = new HashArray<>();
        ticks.forEach(tick -> dateTradingHours
                .put(tick.getDate(), tradingHours));
        MarketPlace marketPlace = new MarketPlace("Oslo Børs",
                "Norway", "Oslo", "NOK", "CET",
                dateTradingHours);

        symbol = new Symbol(60, "Dummy_stock", "DMS", "Lom Børs",
                ticks, marketPlace, null);

        double cash = 103;
        testStrategy = new TestStrategy(symbol, cash);
        analyser = new Analyser.Builder().startDate(20240102)
                .endDate(20240107).cash(cash).showProgress(false)
                .strategies(Arrays.asList(testStrategy))
                .dateList(dateTradingHours.getKeyList()).build();
    }

    @Test
    @DisplayName("Correctly short return")
    void testShortReturn() {
        testStrategy.setShortSignal(date -> date == 20240103);
        testStrategy.setCoverSignal(date -> date == 20240104);

        testStrategy.setShortAllowed(true);
        testStrategy.setLongAllowed(false);

        analyser.runSimulation();

        double simulationReturn = analyser.getResult()
                .getReturn();
        assertEquals(4.85, simulationReturn, 0.01,
                "(103-98)/103 * 100");
    }

    @Test
    @DisplayName("Price increses above 100% when shorting")
    void testShortExtremeIncrease() {
        testStrategy.setShortSignal(date -> date == 20240106);
        testStrategy.setCoverSignal(date -> false); // Never cover

        testStrategy.setShortAllowed(true);
        testStrategy.setLongAllowed(false);

        analyser.runSimulation();

        double simulationReturn = analyser.getResult()
                .getReturn();
        assertEquals(-108.73786, simulationReturn, 0.001,
                "Not shorting 100% og cash, so its less then percent diff short to end price");
    }

    // @Test
    // @DisplayName("Price increses above 100% when shorting")
    // void testShortExtremeIncreaseCloseTrade() {
    // testStrategy.setShortSignal(date -> date == 20240106);
    // testStrategy.setCoverSignal(date -> date == 20240107);

    // testStrategy.setShortAllowed(true);
    // testStrategy.setLongAllowed(false);

    // analyser.runSimulation();

    // double simulationReturn = analyser.getResult().getReturn();
    // assertEquals(-108.73786, simulationReturn, 0.001,
    // "Not shorting 100% og cash, so its less then percent diff short to end
    // price");
    // }

    @Test
    @DisplayName("Return correct when buying and selling on trade")
    void testLongReturn() {
        testStrategy.setLongSinal(date -> date == 20240103);
        testStrategy.setSellSignal(date -> date == 20240104);

        testStrategy.setShortAllowed(false);
        testStrategy.setLongAllowed(true);

        analyser.runSimulation();

        double simulationReturn = analyser.getResult()
                .getReturn();
        assertEquals(-4.854368932, simulationReturn, 0.001,
                "(98-103)/103 * 100");
    }

}
