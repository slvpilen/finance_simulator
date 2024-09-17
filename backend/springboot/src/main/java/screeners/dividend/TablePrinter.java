package screeners.dividend;

import java.util.List;

public class TablePrinter {
        public static void printTable(
                        List<DividendScreenerDTO> screenerSymbols) {
                String leftAlignFormat = "| %-40s | %-10s | %-15s | %-10s |%n";

                // Print table headers
                System.out.format(
                                "+------------------------------------------+------------+-----------------+------------+%n");
                System.out.format(
                                "| Name                                     | Ticker     | Direct-Return   | Updated%n");
                System.out.format(
                                "+------------------------------------------+------------+-----------------+------------+%n");

                // Print table rows
                for (DividendScreenerDTO screenerSymbol : screenerSymbols) {
                        System.out.format(leftAlignFormat,
                                        screenerSymbol.name
                                                        .subSequence(0, Math
                                                                        .min(40, screenerSymbol.name
                                                                                        .length())),
                                        screenerSymbol.ticker,
                                        screenerSymbol.directReturn,
                                        screenerSymbol.updated);
                }

                // Print table footer
                System.out.format(
                                "+------------------------------------------+------------+-----------------+------------+%n");
        }
}
