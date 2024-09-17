package screeners.rsi;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import database.DatabaseService;
import database.Symbol;
import indicators.RelativeStrengthIndex;
import indicators.utils.TimeFrame;
import screeners.TickerList;

public class RSIScreener {

        private final static int RSI_LENGTH = 14;

        public static List<RSIScreenerSymbolDTO> getRSIScreenerSymbols(
                        String symbolListName, int date) {

                int endDate = date;
                int startDate = endDate - 20000;

                List<Symbol> symbols = DatabaseService
                                .getSymbols(symbolListName,
                                                startDate, date);

                List<RelativeStrengthIndex> rsiList = convertToRSIIndicators(
                                symbols);

                List<RSIScreenerSymbolDTO> screenerSymbols = new ArrayList<>();

                for (int i = 0; i < symbols.size(); i++) {
                        Symbol symbol = symbols.get(i);
                        RelativeStrengthIndex rsi = rsiList
                                        .get(i);

                        int closetsDate = -1;
                        double rsiValue = -1;
                        double rsiWeekly = -1;
                        double rsiMonthly = -1;

                        if (rsi != null) {
                                closetsDate = symbol
                                                .getClosestDate(date);

                                rsiValue = getRSI(rsi,
                                                closetsDate,
                                                TimeFrame.DAILY);
                                rsiWeekly = getRSI(rsi,
                                                closetsDate,
                                                TimeFrame.WEEKLY);
                                rsiMonthly = getRSI(rsi,
                                                closetsDate,
                                                TimeFrame.MONTHLY);
                        }

                        RSIScreenerSymbolDTO screenerSymbol = RSIScreenerSymbolDTO
                                        .builder()
                                        .ticker(symbol.getTicker())
                                        .name(symbol.getName())
                                        .rsiDaily(rsiValue)
                                        .rsiWeekly(rsiWeekly)
                                        .updated(closetsDate)
                                        .rsiMonthly(rsiMonthly)
                                        .build();
                        screenerSymbols.add(screenerSymbol);
                }

                return screenerSymbols;
        }

        private static List<RelativeStrengthIndex> convertToRSIIndicators(
                        List<Symbol> symbols) {
                return symbols.stream().map(symbol -> (symbol
                                .getDateList().size() > 0)
                                                ? new RelativeStrengthIndex(
                                                                symbol,
                                                                RSI_LENGTH)
                                                : null)
                                .collect(Collectors.toList());
        }

        private static double getRSI(RelativeStrengthIndex rsi,
                        int date, TimeFrame timeFrame) {
                try {
                        rsi.setTimeFrame(timeFrame);
                        return rsi.getValue(date);
                } catch (Exception e) {
                        System.out.println(
                                        "Error in getting RSI");
                        return -1;
                }
        }

        public static void main(String[] args) {

                int DATE = 20200101;
                List<RSIScreenerSymbolDTO> screenerSymbols = RSIScreener
                                .getRSIScreenerSymbols(
                                                "Country ETFs on US stock exchange",
                                                DATE);

                // sort by rsi weekly
                screenerSymbols.sort((s1, s2) -> Double.compare(
                                s1.rsiWeekly, s2.rsiWeekly));

                TablePrinter.printTable(screenerSymbols);

        }

}
