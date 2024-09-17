package database;

public class TradingHours {

    private final int openTime;
    private final int closeTime;

    public TradingHours(int openTime, int closeTime) {
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    public int getOpenTime() { return openTime; }

    public int getCloseTime() { return closeTime; }

}
