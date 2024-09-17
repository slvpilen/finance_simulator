package database;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Dividend {

    private int date; // yyyymmdd
    private double value;

    @Builder(builderMethodName = "dividendBuilder")
    public Dividend(int date, double value) {
        this.date = date;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Dividend{" + "date=" + date + ", value=" + value
                + '}';

    }

}
