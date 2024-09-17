package database.makroTrend;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Settings {

    // Turn strategy on/off
    @Setter
    protected boolean optimizationMode = false;

    @Setter
    protected int drawdownSelfKill = -20;
    public int stockRankedWithin = 5;
    // protected int tradingDayToBuy = 1; // new month

    public int industriScoreWeightingRoc22 = 1;
    public int industriScoreWeightingRoc43 = 1;
    public int industriScoreWeightingRoc82 = 1;
    public int industriScoreWeightingPredictiveAverage = 1;

    protected final int minimumLiquidity = 10000000;
    @Getter
    @Setter
    protected static int startDateOptimization = 20100104;
    @Getter
    @Setter
    protected static int startDateOptimizationSettings = 20120104; // 20110104
    @Getter
    @Setter
    protected static int endDateOptimization = 20231228;
    protected final int simulationCash = 1000000;

    public boolean isOptimizationMode() { return optimizationMode; }

    @Override
    public String toString() {
        return "Settings [drawdownSelfKill=" + drawdownSelfKill + ", endDateOptimization=" + endDateOptimization
                + ", minimumLiquidity=" + minimumLiquidity + ", optimizationMode=" + optimizationMode
                + ", simulationCash=" + simulationCash + ", startDateOptimization=" + startDateOptimization
                + ", stockRankedWithin=" + stockRankedWithin + ", industriScoreWeightingRoc22="
                + industriScoreWeightingRoc22 + ", industriScoreWeightingRoc43=" + industriScoreWeightingRoc43
                + ", industriScoreWeightingRoc82=" + industriScoreWeightingRoc82
                + ", industriScoreWeightingPredictiveAverage=" + industriScoreWeightingPredictiveAverage + "]";
    }

    public static void main(String[] args) {
        Settings defaultSettings = new Settings();
        System.out.println(defaultSettings);
    }
}