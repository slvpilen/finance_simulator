package database;

import java.util.Arrays;

public enum StockIndustri {

    OIL(), SALMON(),
    SHIPPING(),
    CONSUMER_DISCRETIONARY(),
    INDUSTRIALS(), ALL();

    public static StockIndustri fromIndustryName(String industryName) {

        switch (industryName) {
            case "OIL":
                return StockIndustri.OIL;
            case "SALMON":
                return StockIndustri.SALMON;
            case "SHIPPING":
                return StockIndustri.SHIPPING;
            case "Consumer Discretionary":
                return StockIndustri.CONSUMER_DISCRETIONARY;
            case "Industrials":
                return StockIndustri.INDUSTRIALS;
            case "ALL":
                return StockIndustri.ALL;
            default:
                throw new IllegalArgumentException("No StockIndustri with  " + industryName + " found");
        }
    }

    public static StockIndustri[] getAllIndustries() {
        return Arrays.stream(StockIndustri.values()).filter(industry -> industry != StockIndustri.ALL)
                .toArray(StockIndustri[]::new);
    }

}
