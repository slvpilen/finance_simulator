package portfoliomixer;

import java.util.List;

/*
 * This class is used to test the PortfolioService class.
 * It takes in strategyResult, instead of strategy name and result name.
 * This is done to avoid the need for a database connection.
 */
import utils.HashArray;

public class PortfolioServiceTestClass extends PortfolioService {

    public PortfolioServiceTestClass(
            List<HashArray<Integer, Double>> strategyResults,
            int rebalanceInterval, double gearing) {
        super(null, rebalanceInterval, gearing);
        this.strategyResults = strategyResults;
    }

    @Override
    public void importStrategyResultFromDatabase(
            List<String[]> strategyNameAndResult) {
        return; // Do nothing
    }

    public static void main(String[] args) {
        HashArray<Integer, Double> strategyResult1 = new HashArray<>();
        strategyResult1.put(20240101, 100.0);
        strategyResult1.put(20240102, 101.0);
        strategyResult1.put(20240103, 102.0);
        strategyResult1.put(20240104, 200.0);

        HashArray<Integer, Double> strategyResult2 = new HashArray<>();
        strategyResult2.put(20230101, 200.0);
        strategyResult2.put(20230102, 202.0);
        strategyResult2.put(20230103, 204.0);

        List<HashArray<Integer, Double>> strategyResults = List
                .of(strategyResult1, strategyResult2);

        PortfolioService portfolioService = new PortfolioServiceTestClass(
                strategyResults, 999, 1);

        portfolioService.getPortfolioMixedResult();

    }
}
