package simulator.bases;

import simulator.analyse.Analyser;
import simulator.result.ResultReadable;
import utils.HashArray;
import lombok.Builder;
import java.util.List;
import java.util.stream.Collectors;
import database.DBServiceStrategy;
import database.Symbol;

public class StrategyRunnerBase {

        private Analyser analyser;

        /*
         * @params useUnionSymbolDates: if true dates for the symbols is used, if false
         * the dates for the marketplaces is used
         */

        @Builder(builderMethodName = "strategyRunnerBaseBuilder")
        public StrategyRunnerBase(double cash, int startDate,
                        int endDate,
                        List<StrategyBase> strategys,
                        boolean showProgress,
                        boolean useUnionSymbolDates) {

                List<Integer> unionDates = null;
                if (useUnionSymbolDates) {
                        unionDates = strategys.stream()
                                        .map(strategy -> strategy
                                                        .getSymbol()
                                                        .getDateList())
                                        .distinct()
                                        .flatMap(List::stream)
                                        .distinct().sorted()
                                        .collect(Collectors
                                                        .toList());

                } else {
                        unionDates = strategys.stream()
                                        .map(strategy -> strategy
                                                        .getSymbol()
                                                        .getMarketPlace())
                                        .distinct()
                                        .flatMap(marketPlace -> marketPlace
                                                        .getDates()
                                                        .stream())
                                        .distinct().sorted()
                                        .collect(Collectors
                                                        .toList());
                }

                analyser = new Analyser.Builder()
                                .startDate(startDate)
                                .endDate(endDate).cash(cash)
                                .showProgress(showProgress)
                                .strategies(strategys)
                                .dateList(unionDates).build();
        }

        public void runSimulation() {

                analyser.runSimulation();

                printSimulationResult(analyser.getResult());
        }

        private static void printSimulationResult(
                        ResultReadable result) {
                System.out.println(
                                "---------------------------------------");

                System.out.println("percent/trade: "
                                + String.format("%.2f", result
                                                .getStatsIncreasDTrades()));
                System.out.println("hitrate: " + String.format(
                                "%.2f",
                                result.getStatsHitrate()));
                System.out.println("Number of trades: " + result
                                .getNumberOfClosedTrades());
                System.out.println("Return: " + String.format(
                                "%.2f", result.getReturn()));
                System.out.println("Max Drawdown: "
                                + String.format("%.2f", result
                                                .getMaxDrawdown()));

                System.out.println("percent/trade (long): "
                                + String.format("%.2f", result
                                                .getStatsIncreasDTradesLongTrades()));
                System.out.println("percent/trade (short): "
                                + String.format("%.2f", result
                                                .getStatsIncreasDTradesShortTrades()));
        }

        public void storeInDatabase(String strategyName,
                        String resultName) {
                ResultReadable result = analyser.getResult();
                HashArray<Integer, Double> dailyAccountValue = result
                                .getdailyAccountValues();
                HashArray<Integer, Double> dailyExponation = result
                                .getdailyAccountExponation();

                DBServiceStrategy.insertStrategyResult(
                                strategyName, resultName, false,
                                dailyAccountValue,
                                dailyExponation);

                DBServiceStrategy.insertTradeEvents(strategyName,
                                resultName,
                                result.getTradeEvents(), false);
        }

        public List<Symbol> getHoldings() {
                return getAnalyser().getAccount()
                                .getOpenPositions().stream()
                                .map(position -> position
                                                .getSymbol())
                                .collect(Collectors.toList());
        }

        public Analyser getAnalyser() { return analyser; }

}
