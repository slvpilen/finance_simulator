package database;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import utils.HashArray;

@Getter
public class SimulationResult implements DateValueRetriever {

    private final String strategyName;
    private final String resultName;
    private final HashArray<Integer, Double> ticks;

    @Builder(builderMethodName = "simResultBuilder")
    public SimulationResult(String strategyName,
            String resultName,
            HashArray<Integer, Double> ticks) {
        this.strategyName = strategyName;
        this.resultName = resultName;
        this.ticks = ticks;
    }

    @Override
    public double getClose(int date) { return ticks.get(date); }

    @Override
    public double getAref(int date, int lookback) {
        return ticks.getAref(date, lookback);
    }

    @Override
    public List<Integer> getDateList() {
        return ticks.getKeyList();
    }

    @Override

    public int getClosestDate(int date) {
        return ticks.getClosestKey(date);
    }

    @Override
    public String toString() {
        return strategyName + " " + resultName + " first date:"
                + ticks.getKeyAtIndex(0);
    }

}
