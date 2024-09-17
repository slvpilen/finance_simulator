/**
 * The Analyser class is responsible for iterating through the dates and updating the systeme state.
 * @author Oskar
 * @version 1.0
 * @since 2023-02-01
 */

package simulator.analyse;

import java.util.List;
import lombok.Getter;

import simulator.bases.StrategyBase;
import simulator.result.ResultReadable;

public class Analyser {
    private List<Integer> dateList;
    @Getter
    private Account account;
    private boolean showProgress = false;
    private boolean hasSimulated = false;
    private int startDate, endDate;

    public Analyser(Builder builder) {
        validateInput(builder);

        this.account = new Account(builder.cash);
        this.showProgress = builder.showProgress;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.dateList = makeDateList(startDate, endDate, builder.dateList);

        if (builder.strategies != null) {
            account.addStrategies(builder.strategies);
        }
    }

    // TODO: convert to lombook builder
    public static class Builder {
        private int startDate;
        private int endDate;
        private double cash;
        private boolean showProgress = false;
        private List<StrategyBase> strategies;
        private List<Integer> dateList;

        public Builder startDate(int startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(int endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder cash(double cash) {
            this.cash = cash;
            return this;
        }

        public Builder showProgress(boolean showProgress) {
            this.showProgress = showProgress;
            return this;
        }

        public Builder strategies(List<StrategyBase> strategies) {
            this.strategies = strategies;
            return this;
        }

        public Builder dateList(List<Integer> dateList) {
            this.dateList = dateList; // TODO rename iterationDates
            return this;
        }

        public Analyser build() { return new Analyser(this); }
    }

    public void runSimulation() {
        if (hasSimulated)
            throw new IllegalStateException("Simulation has already been run!");

        if (!account.hasStrategys())
            throw new IllegalStateException("No strategies in account!");

        account.noticeStrategysBeforeSimulation();
        account.setSimulationStarted(true);

        for (int date : dateList) {
            account.iterationDate(date, -1.0);
            if (showProgress) {
                printResultDuringSimulation(date);
            }
        }

        hasSimulated = true;
    }

    private void printResultDuringSimulation(int date) {
        System.out.println("date: " + date + " account value: " + account.getAccountValue() + " DT increase: "
                + account.getResult().getStatsIncreasDTrades());
    }

    public void addStrategy(StrategyBase strategy) {
        account.addStrategy(strategy);
        throw new UnsupportedOperationException("Not handled dateList when adding strategy!");
        // this.dateList = makeDateList(startDate, endDate,
        // account.getStrategyWithMostDates());
    }

    public List<String> getAllSymbolNames() { return account.getAllSymbolNames(); }

    private void validateInput(Builder builder) {
        if (builder.startDate > builder.endDate)
            throw new IllegalArgumentException("Start date is before end date!");

        if (Integer.toString(builder.startDate).length() != 8)
            throw new IllegalArgumentException("start date not correct length: " + builder.startDate);

        if (Integer.toString(builder.endDate).length() != 8)
            throw new IllegalArgumentException("end date not correct length: " + builder.endDate);

        if (builder.cash <= 0)
            throw new IllegalArgumentException("cant make an account with negative cash");
    }

    public void simulateExtraDate(int date) {
        if (!hasSimulated)
            throw new IllegalStateException("Simulation has not been run yet!");

        account.iterationDate(date, -1.0);
        if (showProgress)
            System.out.println("date: " + date + " account value: " + account.getAccountValue());
    }

    /**
     * Returns the result of the analysis.
     *
     * @return the ResultService object containing the analysis result
     */
    public ResultReadable getResult() { return account.getResult(); }

    /**
     * Creates a list of dates based on the provided start and end dates and the
     * list of dates of the strategy with the most dates.
     *
     * @param startDate the start date of the analysis
     * @param endDate   the end date of the analysis
     * @param dates     the list of dates of the strategy with the most dates
     * @return the list of dates to
     * @throws IllegalStateException if the order of dates is not sorted
     */
    private static List<Integer> makeDateList(int startDate, int endDate, List<Integer> dates) {
        int firstDayIndex;
        int lastDayIndex;

        if (dates.contains(startDate)) {
            firstDayIndex = dates.indexOf(startDate);
        } else {
            firstDayIndex = findIndex(dates, startDate);
        }

        if (dates.contains(endDate)) {
            lastDayIndex = dates.indexOf(endDate) + 1;
        } else {
            lastDayIndex = findIndex(dates, endDate);
        }

        return dates.subList(firstDayIndex, lastDayIndex);
    }

    private static int findIndex(List<Integer> arr, int target) {
        int left = 0;
        int right = arr.size();

        while (left < right) {
            int mid = (left + right) / 2;

            if (arr.get(mid) <= target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left;
    }
}
