package database;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TickData {

    private int date; // yyyymmdd
    private double open;
    private double high;
    private double low;
    private double close;
    private double adjClose;
    private double volume;

    // TickDaily
    @Builder(builderMethodName = "tickBuilder")
    public TickData(int date, double open, double high, double low, double close, double adjClose, int volume) {
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.adjClose = adjClose;
        this.volume = volume;
    }

    @Override
    public String toString() {
        return "Symbol{" + "date=" + date + ", open=" + open + ", high=" + high + ", low=" + low + ", close=" + close
                + ", adjClose=" + adjClose + ", volume=" + volume + '}';
    }

}
