package strategies.connorsAi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import simulator.bases.StrategyBase;
import simulator.bases.StrategyRunnerBase;
import strategies.GlobalSettings;
import utils.Date;
import database.DatabaseService;
import database.Symbol;
import lombok.Builder;

public class StrategyRunner extends StrategyRunnerBase {

    @Builder(builderMethodName = "strategyRunnerBuilder")
    public StrategyRunner(double cash, int startDate,
            int endDate, List<StrategyBase> strategys,
            boolean showProgress) {
        super(cash, startDate, endDate, strategys, showProgress,
                true);
    }

    public final static int START_DATE = 20160101;
    public final static int END_DATE = Date.getTodaysDate();;
    public static final double CASH = 10000000;// 25000.0;

    public static void main(String[] args) {
        String strategyName = ConnorsAiStrategy.STRATEGY_NAME;
        String resultName = "v_10_sp500";

        System.out.println("Simulation started...");

        // TODO: set values

        List<String> tickers = Arrays.asList("^GSPC"); // ^GSPC

        try {
            List<StrategyBase> strategies = new ArrayList<>();
            tickers.forEach(ticker -> {
                Symbol symbol = DatabaseService
                        .getSymbol(ticker);
                if (symbol.getDateList().size() == 0) {
                    // System.out.println("No data for symbol: " + symbolName);
                    return;
                }
                strategies.add(
                        new ConnorsAiStrategy(symbol, CASH));
            });

            StrategyRunnerBase strategyRunner = new StrategyRunner.StrategyRunnerBuilder()
                    .cash(CASH).startDate(START_DATE)
                    .endDate(END_DATE).strategys(strategies)
                    .showProgress(true).build();

            strategyRunner.runSimulation();
            strategyRunner.storeInDatabase(strategyName,
                    resultName);
            if (GlobalSettings.storeHoldingsInDatabase) {
                DatabaseService.storeHoldings(strategyName,
                        strategyRunner.getHoldings(),
                        Date.getTodaysDate(), resultName);
            }
        } catch (Exception e) {
            System.out.println(e);
            System.out.println(e.getMessage());
        }

        if (GlobalSettings.closeDatabaseConnection) {
            DatabaseService.close();
        }

    }
}
