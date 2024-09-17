package screeners.dividend;

import java.util.ArrayList;
import java.util.List;

import database.DatabaseService;
import database.Symbol;
import screeners.TickerList;

public class DividendScreener {

    public static List<DividendScreenerDTO> getDividendScreenerDTOs(
            String symbolListName, int date) {
        int startDate = date - 20000; // 2 years of date
        List<Symbol> symbols = DatabaseService
                .getSymbols(symbolListName, startDate, date);

        return getDividendScreenerDTOs(symbols, date);
    }

    private static List<DividendScreenerDTO> getDividendScreenerDTOs(
            List<Symbol> symbols, int date) {

        List<DividendScreenerDTO> screenerSymbols = new ArrayList<>();

        Symbol symbol;
        for (int i = 0; i < symbols.size(); i++) {
            symbol = symbols.get(i);
            double directReturn = -1;
            int closetsDate = -1;
            try {
                closetsDate = symbol.getClosestDate(date);

                directReturn = symbol
                        .getDirectReturn(closetsDate);
                // 2 decimals
                directReturn = ((double) ((int) (directReturn
                        * 100))) / 100;
            } catch (Exception e) {
            }

            DividendScreenerDTO screenerSymbol = DividendScreenerDTO
                    .builder().ticker(symbol.getTicker())
                    .name(symbol.getName())
                    .directReturn(directReturn)
                    .updated(closetsDate).build();
            screenerSymbols.add(screenerSymbol);
        }

        return screenerSymbols;
    }

    public static void main(String[] args) {

        int DATE = 20240818;
        List<DividendScreenerDTO> screenerSymbols = DividendScreener
                .getDividendScreenerDTOs(
                        "Nordea Stabil Aksjer Global", DATE);

        // sort by rsi weekly
        screenerSymbols.sort((s1, s2) -> Double
                .compare(s2.directReturn, s1.directReturn));

        TablePrinter.printTable(screenerSymbols);

    }

}
