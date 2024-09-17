package screeners.dividend;

import lombok.Builder;

@Builder
public class DividendScreenerDTO {

    public String ticker;
    public String name;
    public double directReturn;
    // public int daysSinceExDividend;
    // public int avgDaysUpp;
    // public double avgIncrease;
    // public double hitrate;
    public int updated;

    @Override
    public String toString() {
        return "DividendScreenerDTO{" + "ticker='" + ticker
                + '\'' + ", name='" + name + '\''
                + ", directReturn=" + directReturn + ", updated="
                + updated + '}';
    }

}
