package database;

import java.util.List;

import utils.HashArray;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder(builderMethodName = "marketPlaceBuilder")
public class MarketPlace {

    private final String name;
    private final String country;
    private final String city;
    private final String currency;
    private final String timeZone;
    private final HashArray<Integer, TradingHours> dateTradingHours;

    /**
     * Checks if the market is open at the given date.
     * 
     * @param date
     * @return true if the market is open at the given date, false otherwise.
     */
    public boolean hasDate(int date) {
        return dateTradingHours.contains(date);
    }

    public int closingTime(int date) {
        return dateTradingHours.get(date).getCloseTime();
    }

    public int openingTime(int date) {
        return dateTradingHours.get(date).getOpenTime();
    }

    public List<Integer> getDates() {
        return dateTradingHours.getKeyList();
    }

    public int getYesterdayDate(int date) {
        return dateTradingHours.getArefKey(date, 1);
    }

    public int getClosestDate(int date) {
        return dateTradingHours.getClosestKey(date);
    }

    @Override
    public String toString() {
        return "MarketPlace{" + "name='" + name + '\''
                + ", country='" + country + '\'' + ", city='"
                + city + '\'' + ", currency='" + currency + '\''
                + ", timeZone='" + timeZone + '\'' + '}';
    }

    public static void main(String[] args) {
        TradingHours osloTradingHours = new TradingHours(900,
                1600);

        HashArray<Integer, TradingHours> dateTradingHours = new HashArray<>();
        dateTradingHours.put(20240102, osloTradingHours);
        dateTradingHours.put(20240103, osloTradingHours);
        dateTradingHours.put(20240104, osloTradingHours);

        MarketPlace marketPlace = new MarketPlace.MarketPlaceBuilder()
                .name("Oslo Børs").country("Norway").city("Oslo")
                .currency("NOK").timeZone("CET")
                .dateTradingHours(dateTradingHours).build();

        System.out.println(marketPlace);

        System.out.println("Shoud be true: "
                + marketPlace.hasDate(20240102));
        System.out.println("Shoud be false: "
                + marketPlace.hasDate(20220102));
    }
}
