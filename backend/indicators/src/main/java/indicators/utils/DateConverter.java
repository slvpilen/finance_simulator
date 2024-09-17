package indicators.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class DateConverter {

        public static List<Integer> convertDates(
                        List<Integer> dateList,
                        TimeFrame timeFrame) {
                List<LocalDate> dates = dateList.stream().map(
                                date -> LocalDate.parse(String
                                                .valueOf(date),
                                                DateTimeFormatter.BASIC_ISO_DATE))
                                .collect(Collectors.toList());

                switch (timeFrame) {
                case TimeFrame.WEEKLY:
                        return getLatestDayOfEachWeek(dates);
                case TimeFrame.MONTHLY:
                        return getLatestDayOfEachMonth(dates);
                default:
                        throw new IllegalArgumentException(
                                        "TimeFrame not supported: "
                                                        + timeFrame);
                }

        }

        private static List<Integer> getLatestDayOfEachWeek(
                        List<LocalDate> dates) {
                // Use a fixed locale, for example, US, to avoid locale-specific week
                // definitions
                TemporalField weekOfYear = WeekFields
                                .of(Locale.US)
                                .weekOfWeekBasedYear();

                // Group dates by week number and year to handle year transitions correctly
                Map<Integer, Map<Integer, List<LocalDate>>> groupedByYearAndWeek = dates
                                .stream()
                                .collect(Collectors.groupingBy(
                                                date -> date.getYear(),
                                                Collectors.groupingBy(
                                                                date -> date.get(
                                                                                weekOfYear))));

                List<LocalDate> latestDates = new ArrayList<>();

                // Collect the latest date of each week
                for (Map<Integer, List<LocalDate>> yearGroup : groupedByYearAndWeek
                                .values()) {
                        for (List<LocalDate> weekDates : yearGroup
                                        .values()) {
                                LocalDate latestDate = Collections
                                                .max(weekDates);
                                latestDates.add(latestDate);
                        }
                }

                // Sort the latest dates
                latestDates.sort(Comparator.naturalOrder());

                // Convert the sorted dates to the desired integer format
                DateTimeFormatter formatter = DateTimeFormatter.BASIC_ISO_DATE;
                return latestDates.stream().map(date -> Integer
                                .parseInt(date.format(
                                                formatter)))
                                .collect(Collectors.toList());
        }

        private static List<Integer> getLatestDayOfEachMonth(
                        List<LocalDate> dates) {
                Map<Integer, List<LocalDate>> groupedByMonth = dates
                                .stream()
                                .collect(Collectors.groupingBy(
                                                date -> date.getYear()
                                                                * 100
                                                                + date.getMonthValue()));

                List<LocalDate> latestDates = new ArrayList<>();

                for (List<LocalDate> monthDates : groupedByMonth
                                .values()) {
                        LocalDate latestDate = Collections
                                        .max(monthDates);
                        latestDates.add(latestDate);
                }

                latestDates.sort(Comparator.naturalOrder());

                return latestDates.stream().map(date -> Integer
                                .parseInt(date.format(
                                                DateTimeFormatter.BASIC_ISO_DATE)))
                                .collect(Collectors.toList());
        }
}
