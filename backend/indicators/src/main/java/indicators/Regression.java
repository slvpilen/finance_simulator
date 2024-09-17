package indicators;

import java.util.ArrayList;
import java.util.List;

import database.DatabaseService;
import database.DateValueRetriever;
import simulator.bases.IndicatorBase;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

public class Regression extends IndicatorBase {

    List<DateValueRetriever> independentdateValueRetrievers;

    private List<Integer> commonDates;

    private List<Double> dependent;
    private List<List<Double>> independents;

    private final int lookback;

    public Regression(
            DateValueRetriever dependentdateValueRetriever,
            List<DateValueRetriever> independentdateValueRetrievers,
            int lookback) {
        super(dependentdateValueRetriever);

        this.lookback = lookback;
        this.independentdateValueRetrievers = independentdateValueRetrievers;
        dependent = new ArrayList<>();
        independents = new ArrayList<>();

        commonDates = generateCommonDays(
                dependentdateValueRetriever,
                independentdateValueRetrievers);

        commonDates.sort(Integer::compareTo);

    }

    /*
     * Returns the percent difference between the excpected value and the acctual
     * value for a given date. A highe value indicates that the acctual value is
     * lower than the excpected. A lowe value indicates that the acctual value is
     * higher than the excpected.
     */
    public double getValue(int date) {

        double excpected = getExcpectedValue(date);

        boolean missingDateInCommonDates = excpected == -1000;
        if (missingDateInCommonDates) {
            date = findFirstCommonDate(date);

            boolean hasNoEarlierCommonDate = date == -1;
            if (hasNoEarlierCommonDate) {
                // System.out.println("Warning: Regression missing data for date: " + date);
                return 0; // Missing data
            }
            excpected = getExcpectedValue(date);
        }
        boolean missingOrNotEnohgDate = excpected == -1000;
        if (missingOrNotEnohgDate) {
            // System.out.println("Warning: Reression missing data for date: " + date);
            return -1; // Missing data
        }

        double acctual = dateValueRetriever.getClose(date);
        double percentDiff = (excpected - acctual) / acctual
                * 100;

        return percentDiff;
    }

    // TODO optimize
    private int findFirstCommonDate(int date) {
        if (commonDates.contains(date)) {
            return date;
        }

        while (!commonDates.contains(date)) {
            date--;
            if (commonDates.contains(date)) {
                return date;
            }
            if (date < 20000101) {
                return -1;
            }
        }

        return -1;
    }

    public double getExcpectedValue(int date) { // regressionAnalysis() {
        try {
            updateDependenandIndependentLists(date);
        } catch (Exception IllegalArgumentException) {
            return -1000; // Not enhoug data or missing date. Add a warning?
        }
        if (dependent.size() < 2 || independents.isEmpty()) {
            throw new IllegalArgumentException(
                    "Insufficient data.");
        }

        // Prepare data for OLSMultipleLinearRegression
        double[] y = dependent.stream()
                .mapToDouble(Double::doubleValue).toArray();
        double[][] x = new double[dependent.size()][independents
                .size()];
        for (int i = 0; i < dependent.size(); i++) {
            for (int j = 0; j < independents.size(); j++) {
                x[i][j] = independents.get(j).get(i);
            }
        }

        // System.out.println("x: " + x[2][2]);

        OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
        regression.newSampleData(y, x);

        // Perform regression analysis
        double[] beta = regression
                .estimateRegressionParameters();
        // double[] standardErrors =
        // regression.estimateRegressionParametersStandardErrors();
        // double rSquared = regression.calculateRSquared();
        // double adjustedRSquared = regression.calculateAdjustedRSquared();

        double expectedValue = beta[0]; // intercept
        // Output results
        // System.out.println("Intercept: " + beta[0]);
        for (int i = 1; i < beta.length; i++) {
            expectedValue += beta[i] * independents.get(i - 1)
                    .get(independents.get(i - 1).size() - 1);
            // System.out.println("Coefficient for independent" + i + ": " + beta[i]);
        }
        // System.out.println("R-Squared: " + rSquared);
        // System.out.println("Adjusted R-Squared: " + adjustedRSquared);

        return expectedValue;

    }

    // TODO: This is not good if one has extremly low amount of data...
    protected static List<Integer> generateCommonDays(
            DateValueRetriever dependentdateValueRetriever,
            List<DateValueRetriever> independentdateValueRetrievers) {

        List<Integer> commonDates = dependentdateValueRetriever
                .getDateList();
        for (DateValueRetriever dvr : independentdateValueRetrievers) {
            commonDates.retainAll(dvr.getDateList());
        }
        return commonDates;
    }

    private void updateDependenandIndependentLists(int date) {
        int endIndex = commonDates.indexOf(date);
        int startIndex = endIndex - lookback;
        if (endIndex == -1) {
            throw new IllegalArgumentException(
                    "date not found in common dates.");
        }
        if (startIndex < 0) {
            throw new IllegalArgumentException(
                    "Not enoghe data to perform regression with lookback: "
                            + lookback);
        }

        List<Integer> commonDatesRezized = new ArrayList<>(
                commonDates).subList(startIndex, endIndex);

        dependent.clear();
        for (Integer commonDate : commonDatesRezized) {
            if (commonDate > date) {
                break;
            }
            dependent.add(
                    dateValueRetriever.getClose(commonDate));
        }

        independents.clear();
        for (DateValueRetriever dvr : independentdateValueRetrievers) {
            List<Double> independentData = new ArrayList<>();
            for (Integer commonDate : commonDatesRezized) {
                if (commonDate > date) {
                    break;
                }
                independentData.add(dvr.getClose(commonDate));
            }
            independents.add(independentData);
        }
    }

    public static void main(String[] args) {
        DateValueRetriever eqnr = DatabaseService
                .getStock("EQNR"); // dependent
        List<DateValueRetriever> independents = new ArrayList<>();
        // Adding crude oil price
        independents.add(DatabaseService.getCommodity("BZ=F"));
        // Adding DNO
        independents.add(DatabaseService.getSymbol("DNO"));

        int lookback = 1000;
        Regression regression = new Regression(eqnr,
                independents, lookback);

        List<Integer> commonDays = generateCommonDays(eqnr,
                independents);

        commonDays = commonDays.subList(lookback,
                commonDays.size() - 1);
        System.out.println("date;excpected;acctual;diff");
        for (Integer date : commonDays) {
            System.out.println(date + ";"
                    + regression.getExcpectedValue(date) + ";"
                    + eqnr.getClose(date) + ";"
                    + (eqnr.getClose(date) - regression
                            .getExcpectedValue(date)));
            // System.out.println("Excpected value for " + date + ": " +
            // regression.getValue(date));
        }

        int date = 20231214;
        System.out.println(
                "Procent diff from acctual to excpected value for "
                        + date + ": "
                        + regression.getValue(date));

    }

}
