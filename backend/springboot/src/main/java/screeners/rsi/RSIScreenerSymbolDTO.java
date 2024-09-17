package screeners.rsi;

import lombok.Builder;

// TODO use a serilizer to convert this to json instead
// Rename dto?

@Builder
public class RSIScreenerSymbolDTO {

    public String ticker;
    public String name;
    public double rsiDaily;
    public double rsiWeekly;
    public double rsiMonthly;
    public int updated;

    @Override
    public String toString() {
        return "RSIScreenerSymbolModel{" + "ticker='" + ticker
                + '\'' + ", name='" + name + '\'' + ", rsiDaily="
                + rsiDaily + ", rsiWeekly=" + rsiWeekly
                + ", rsiMonthly=" + rsiMonthly + '}';
    }

}
