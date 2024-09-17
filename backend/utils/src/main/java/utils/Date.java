package utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Date {

    public static int extractYear(int date) {
        int yyyy = date / 10000;
        return yyyy;
    }

    public static int extractMonth(int date) {
        int MM = (date % 10000) / 100;
        return MM;
    }

    public static int extractMonthAndDay(int date) {
        int MMdd = date % 10000;
        return MMdd;
    }

    public static int extractDayOfMonth(int date) {
        int dd = date % 100;
        return dd;
    }

    public static int extractDayOfWeek(int date) {
        LocalDate localDate = LocalDate.of(extractYear(date),
                extractMonth(date), extractDayOfMonth(date));
        return localDate.getDayOfWeek().getValue();
    }

    public static int getTodaysDate() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("yyyyMMdd");
        String formattedDate = today.format(formatter);

        int yyyyMMdd = Integer.parseInt(formattedDate);
        return yyyyMMdd;
    }

    public static int getNowTime() {
        LocalTime timeNow = LocalTime.now();

        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("HHmm");
        String formattedTime = timeNow.format(formatter);

        int hhmm = Integer.parseInt(formattedTime);
        return hhmm;
    }

}
