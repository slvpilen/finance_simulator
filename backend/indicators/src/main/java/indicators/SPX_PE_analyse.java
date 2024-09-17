package indicators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import database.DatabaseService;
import database.Makro;
import database.Symbol;
import utils.Date;
import utils.HashArray;

public class SPX_PE_analyse {

    private static Makro convertToLatestDate(Makro makro, Symbol symbol) {

        HashArray<Integer, Double> ticks = new HashArray<>();

        List<Integer> dates = symbol.getDateList();
        int latestMonth = 0;
        for (Integer date : dates) {
            int month = Date.extractMonth(date);
            if (month > latestMonth) {
                String monthString = month < 10 ? "0" + month : String.valueOf(month);
                String convertedDate = String.valueOf(Date.extractYear(date)) + monthString + "01";
                int convertedDateInt = Integer.parseInt(convertedDate);
                if (makro.getTicks().hasData(convertedDateInt)) {
                    // if (makro.getClose(convertedDateInt) < 20)
                    // continue; // This is a unexcpected side effect of method
                    ticks.put(date, makro.getClose(convertedDateInt));
                }
            }
            latestMonth = month;
        }

        return Makro.makroBuilder().type(makro.getType()).frequence(makro.getFrequence()).ticks(ticks).build();
    }

    public static void main(String[] args) {
        // Fetch data from database
        Symbol SP_500 = DatabaseService.getSymbol("^GSPC");
        Makro SP_500_PE = DatabaseService.getMakro("S&P_PE");

        SP_500_PE = convertToLatestDate(SP_500_PE, SP_500);

        List<Integer> commonDates = Regression.generateCommonDays(SP_500, Arrays.asList(SP_500_PE));

        int tradingDaysInFiveYears = 1100;

        ROC rateOfChange = new ROC(SP_500, tradingDaysInFiveYears);
        HashArray<Integer, Double> ticks_return = new HashArray<>();

        RelativeStrengthIndex rsi = new RelativeStrengthIndex(SP_500, 14);
        HashArray<Integer, Double> ticks_RSI = new HashArray<>();

        int firstDateWithEnoughData = SP_500.getDateList().get(tradingDaysInFiveYears + 1);
        // Calculate return for next 5 years
        for (int date : SP_500.getDateList()) {
            if (date < firstDateWithEnoughData) {
                continue;
            }

            int dateFiveYearsAgo = SP_500.getTickDatas().getArefKey(date, tradingDaysInFiveYears);
            rateOfChange.iterationDate(date, -1);

            ticks_return.put(dateFiveYearsAgo, 100 + rateOfChange.getValue());
            ticks_RSI.put(date, rsi.getValue(date));
        }

        Makro SP_500_5_years_return = Makro.makroBuilder().type("S&P_500_5_years_return").frequence("daily")
                .ticks(ticks_return).build();
        Makro SP_500_RSI = Makro.makroBuilder().type("RSI").frequence("daily").ticks(ticks_RSI).build();

        Regression regression;
        int count = 0;
        // for (int date : commonDates) {
        // if (count < 202) {
        // count++;
        // continue;
        // }
        // regression = new Regression(SP_500_5_years_return, Arrays.asList(SP_500_PE),
        // 240);

        // // commonDates = commonDates.subList(240, commonDates.size() - 1);
        // // System.out.println("date;excpected;acctual;diff");
        // // for (Integer date : commonDates) {
        // // System.out.println(regression.getValue(date));
        // // // System.out.println(date + ";" + regression.getExcpectedValue(date) +
        // ";" +
        // // // SP_500.getClose(date) + ";"
        // // // + (SP_500.getClose(date) - regression.getExcpectedValue(date)));

        // // }
        // System.out.println("date: " + date + " value: " + regression.getValue(date));
        // count++;
        // }
        int date = 20181001;
        regression = new Regression(SP_500_5_years_return, Arrays.asList(SP_500_PE, SP_500_RSI), 900);
        System.out.println(regression.getValue(date));

        DatabaseService.close();
    }

}
