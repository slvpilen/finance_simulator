/**
* This indicator calculate the predictive average return based on the same month in previous years' performances.
* 
* @author Oskar
* @version 1.0
* @since 2024-01-04
*/

package indicators;

import java.util.HashMap;
import java.util.List;

import database.DateValueRetriever;
import database.DatabaseService;
import simulator.bases.IndicatorBase;
import utils.Date;

public class PredictiveAverage extends IndicatorBase {

    private HashMap<Integer, double[]> monthlyReturn = new HashMap<>();
    private int numberOfLookbackYears;
    private HashMap<Integer, double[]> yearToMonthlyValues = new HashMap<>();
    // yearToMonthlyValues maps each year to an array of monthly values.
    // The array indices represent months starting from January (index 1) onwards.
    // This structure is used for dynamic programming to enhance performance.

    public PredictiveAverage(
            DateValueRetriever dateValueRetriever,
            int numberOfLookbackYears) {
        super(dateValueRetriever);
        this.numberOfLookbackYears = numberOfLookbackYears;

        generateMonthlyReturn();
    }

    @Override
    public double getValue(int date) {
        int year = Date.extractYear(date);
        int month = Date.extractMonth(date);

        if (yearToMonthlyValues.containsKey(year)) {
            return yearToMonthlyValues.get(year)[month];
        }

        HashMap<Integer, double[]> filteredMap = new HashMap<>(
                monthlyReturn);
        filteredMap.entrySet()
                .removeIf(entry -> entry.getKey() >= year
                        || entry.getKey() < year
                                - numberOfLookbackYears);

        double[] calculatedMonthlyAverage = calculateMonthlyAverages(
                filteredMap);
        yearToMonthlyValues.put(year, calculatedMonthlyAverage);

        return calculatedMonthlyAverage[month];
    }

    private static double[] calculateMonthlyAverages(
            HashMap<Integer, double[]> filteredMap) {
        double[] monthlyAverages = new double[13];
        int yearsCount = filteredMap.size();

        if (yearsCount > 0) {
            double[] monthlySum = new double[13]; // Index 0 will be unused

            // Sum up values for each month across all years
            for (double[] yearlyValues : filteredMap.values()) {
                for (int month = 1; month <= 12; month++) {
                    monthlySum[month] += yearlyValues[month];
                }
            }

            // Calculate the average for each month
            for (int month = 1; month <= 12; month++) {
                monthlyAverages[month] = monthlySum[month]
                        / yearsCount;
            }
        }

        return monthlyAverages;
    }

    private void generateMonthlyReturn() {
        List<Integer> dateList = dateValueRetriever
                .getDateList();

        int firstDate = dateList.get(0);

        int lastMonth = Date.extractMonth(firstDate);
        int lastYear = Date.extractYear(firstDate);

        double firstDayOfMonthValue = dateValueRetriever
                .getClose(firstDate);
        double previusClose = 0;

        for (Integer date : dateList) {
            int currentYear = Date.extractYear(date);

            if (monthlyReturn.isEmpty()
                    || currentYear != lastYear) {
                double[] monthsList = new double[13]; // One each Month. 0 is not used, because januar is 1
                monthlyReturn.put(currentYear, monthsList);
            }

            int currentMonth = Date.extractMonth(date);

            if (currentMonth != lastMonth) {
                double percentChangeLastMonth = (previusClose
                        - firstDayOfMonthValue)
                        / firstDayOfMonthValue * 100;
                monthlyReturn.get(
                        lastYear)[lastMonth] = percentChangeLastMonth;

                firstDayOfMonthValue = dateValueRetriever
                        .getClose(date);
            }
            previusClose = dateValueRetriever.getClose(date);

            lastYear = currentYear;
            lastMonth = currentMonth;

        }
    }

    /*
     * Test on symbol
     */
    public static void main(String[] args) {

        DateValueRetriever dateValueRetriever = DatabaseService
                .getSymbol("AKRBP");
        PredictiveAverage predictiveAverage = new PredictiveAverage(
                dateValueRetriever, 10);
        predictiveAverage.iterationDate(20241002, -1);

        double predictiveAverageJanuar = predictiveAverage
                .getValue();
        System.out.println(predictiveAverageJanuar);

        DatabaseService.close();
    }

}
