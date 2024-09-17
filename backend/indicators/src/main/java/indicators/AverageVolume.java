package indicators;

import database.DatabaseService;
import database.Symbol;
import simulator.bases.Indicator;
import simulator.bases.IndicatorStockOnlyBase;

public class AverageVolume extends IndicatorStockOnlyBase {

    private int length;

    public AverageVolume(Symbol symbol, int length) {
        super(symbol);
        this.length = length;
    }

    @Override
    public double getValue() { return getValue(length); }

    public double getValue(int length) {
        if (date == 0) {
            throw new IllegalArgumentException("Date not set.");
        }

        double volumeSum = 0;
        int validDays = 0;

        if (!symbol.hasData(date, length)) {
            System.out.println("WARNING: Volume for '" + date + "' and lookback: " + length
                    + " is not in THE database. Symbol: " + symbol);
            return -1;
        }

        for (int i = 0; i < length; i++) {
            double volume = symbol.getArefVolume(date, i);
            double close = symbol.getArefClose(date, i);
            volumeSum += (volume * close);

            if (volume > 0 && close > 0) {
                validDays++;
            }
        }

        double averageVolume = validDays > 0 ? volumeSum / validDays : -1;

        return averageVolume;
    }

    // Test
    public static void main(String[] args) {
        Symbol symbol = DatabaseService.getSymbol("MOWI");
        System.out.println("Stock: " + symbol);

        Indicator averageVolumeIndicatorLength5 = new AverageVolume(symbol, 5);
        averageVolumeIndicatorLength5.iterationDate(20231101, -1);
        double averageVolume = averageVolumeIndicatorLength5.getValue();
        System.out.println("Average volume: " + averageVolume);
    }
}
