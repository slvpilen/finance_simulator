package screeners.rsi;

import java.util.List;

public class TablePrinter {
        public static void printTable(
                        List<RSIScreenerSymbolDTO> screenerSymbols) {
                String leftAlignFormat = "| %-20s | %-10s | %-10s | %-10s | %-10s | %-20s |%n";

                // Print table headers
                System.out.format(
                                "+----------------------+------------+------------+------------+------------+----------------------+%n");
                System.out.format(
                                "| Name                 | Ticker     | RSI-Daily  | RSI-Weekly | RSI-Monthly| Updated              |%n");
                System.out.format(
                                "+----------------------+------------+------------+------------+------------+----------------------+%n");

                // Print table rows
                for (RSIScreenerSymbolDTO screenerSymbol : screenerSymbols) {
                        System.out.format(leftAlignFormat,
                                        screenerSymbol.name,
                                        screenerSymbol.ticker,
                                        screenerSymbol.rsiDaily,
                                        screenerSymbol.rsiWeekly,
                                        screenerSymbol.rsiMonthly,
                                        screenerSymbol.updated);
                }

                // Print table footer
                System.out.format(
                                "+----------------------+------------+------------+------------+------------+----------------------+%n");
        }
}
