package portfoliomixer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import database.DBServiceStrategy;
import database.apiModels.LineChartData;
import utils.HashArray;

public class PortfolioService {

    protected List<HashArray<Integer, Double>> strategyResults;
    protected int rebalanceInterval;
    protected double gearing;
    private HashArray<Integer, Integer> allDates; // All dates in all strategies (sorted)

    public PortfolioService(List<String[]> strategyNameAndResult,
            int rebalanceInterval, double gearing) {
        validateInput(rebalanceInterval, gearing);
        importStrategyResultFromDatabase(strategyNameAndResult);
        this.rebalanceInterval = rebalanceInterval;
        this.gearing = gearing;
    }

    private void validateInput(int rebalanceInterval,
            double gearing) {
        if (rebalanceInterval <= 0) {
            throw new IllegalArgumentException(
                    "Rebalance interval must be greater than 0");
        }
        if (gearing <= 0) {
            throw new IllegalArgumentException(
                    "Gearing must be greater than 0");
        }
    }

    protected void importStrategyResultFromDatabase(
            List<String[]> strategyNameAndResult) {
        strategyResults = strategyNameAndResult.stream()
                .map(strategy -> DBServiceStrategy
                        .getStrategyResultAsHashArray(
                                strategy[0], strategy[1]))
                .collect(Collectors.toList());
    }

    public LineChartData getPortfolioMixedResult() {
        setAllDates();
        normalizedStrategies();
        applyGearing();
        LineChartData result = calculatePortfolioMixedResult();

        return result;
    }

    private void setAllDates() {
        if (strategyResults.isEmpty()) {
            return; // Return empty if no data
        }
        allDates = new HashArray<>();

        strategyResults.forEach(strategyResult -> {
            strategyResult.getKeyList().forEach(key -> {
                allDates.put(key, -1); // Use only keys
            });
        });

        allDates.sortBasedKey();
    }

    private void normalizedStrategies() {
        if (allDates.isEmpty()) {
            return; // Return empty if no data
        }

        List<HashArray<Integer, Double>> normalizedStrategyResults = new ArrayList<>();

        // Add 100 until the first date (to make sure the first date is the same in
        // every strategy)
        for (HashArray<Integer, Double> strategyResult : strategyResults) {
            if (strategyResult.isEmpty()) {
                continue;
            }
            HashArray<Integer, Double> normalizedStrategyResult = new HashArray<>();
            normalizedStrategyResults
                    .add(normalizedStrategyResult);

            int firstDateInStrat = strategyResult
                    .getKeyAtIndex(0);

            double previuesValueNormalized = 100;

            for (int date : allDates.getKeyList()) {
                if (date <= firstDateInStrat) {
                    normalizedStrategyResult.put(date, 100.0);
                } else if (!strategyResult.contains(date)) {
                    normalizedStrategyResult.put(date,
                            previuesValueNormalized);
                } else {
                    double previuesValueUnNormalized = strategyResult
                            .getAref(date, 1);
                    double currentValueUnNormalized = strategyResult
                            .get(date);

                    double newValueNormalized = (((currentValueUnNormalized
                            - previuesValueUnNormalized)
                            / previuesValueUnNormalized) + 1)
                            * previuesValueNormalized;

                    normalizedStrategyResult.put(date,
                            newValueNormalized);

                    previuesValueNormalized = newValueNormalized; // Update previus value
                }
            }
        } ;
        strategyResults = normalizedStrategyResults;

    }

    private void applyGearing() {
        if (gearing == 1.0) {
            return; // No gearing
        }

        List<HashArray<Integer, Double>> strategyResultsWithGearing = new ArrayList<>();

        for (HashArray<Integer, Double> strategyResult : strategyResults) {
            HashArray<Integer, Double> strategyResultWithGearing = new HashArray<>();

            double previusValue = 100;
            double previusExpo = previusValue * gearing;
            double previusLoan = previusExpo - previusValue;
            strategyResultWithGearing.put(
                    allDates.getKeyAtIndex(0), previusValue);
            for (int date : allDates.getKeyList().subList(1,
                    allDates.size())) {
                if (previusValue <= 0) {
                    strategyResultWithGearing.put(date,
                            previusValue);
                    continue;
                }
                double currentValueNoGearing = strategyResult
                        .get(date);
                double previuesValueNoGearing = strategyResult
                        .getAref(date, 1);
                double percentIncreaseNoGearing = (currentValueNoGearing
                        - previuesValueNoGearing)
                        / previuesValueNoGearing; // Format 90% as 0.9

                // double loan =
                double newExpo = previusExpo
                        * (1 + percentIncreaseNoGearing);
                double newValue = newExpo - previusLoan;

                strategyResultWithGearing.put(date, newValue);

                previusValue = newValue;
                previusExpo = previusValue * gearing;
                previusLoan = previusExpo - previusValue;
            }
            strategyResultsWithGearing
                    .add(strategyResultWithGearing);
        }
        strategyResults = strategyResultsWithGearing;
    }

    /*
     * Rebalance the portfolio every rebalanceInterval days happens here as well
     */
    protected LineChartData calculatePortfolioMixedResult() {
        LineChartData result = new LineChartData();
        result.setType("Portfolio");
        result.setFrequence("daily");

        Double[] previusStrategyValues = new Double[strategyResults
                .size()];
        for (int i = 0; i < previusStrategyValues.length; i++) {
            previusStrategyValues[i] = 100.0;
        }
        int rebalanceCounter = 0;

        double portfolioValue;
        for (int date : allDates.getKeyList()) {
            if (allDates.getKeyAtIndex(0) == date) {
                result.addDataPoint(date, 100.0);
                continue;
            } else {
                portfolioValue = 0;

                // Rebalance
                if (rebalanceCounter == rebalanceInterval) {
                    rebalanceCounter = 0;
                    double totalValue = 0;
                    for (int i = 0; i < strategyResults
                            .size(); i++) {
                        totalValue += previusStrategyValues[i];
                    }
                    for (int i = 0; i < strategyResults
                            .size(); i++) {
                        previusStrategyValues[i] = totalValue
                                / strategyResults.size();
                    }
                }

                // Calculate portfolio value
                for (int i = 0; i < strategyResults
                        .size(); i++) {
                    HashArray<Integer, Double> strategyResult = strategyResults
                            .get(i);
                    double percentIncrease = 1 + ((strategyResult
                            .get(date)
                            - strategyResult.getAref(date, 1))
                            / strategyResult.getAref(date, 1));
                    portfolioValue += previusStrategyValues[i]
                            * percentIncrease;
                    previusStrategyValues[i] = previusStrategyValues[i]
                            * percentIncrease; // Update previus value for
                                               // next iteration
                }
                portfolioValue /= strategyResults.size();
                portfolioValue = Math
                        .round(portfolioValue * 100.0) / 100.0;
                result.addDataPoint(date, portfolioValue);

                rebalanceCounter++;
            }

        }

        return result;
    }

}