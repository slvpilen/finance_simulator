package indicators.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DateConverterTest {
        @Test
        @DisplayName("Daily to weekly conversion test")
        void convertToWeeklyTest() {
                List<Integer> dateList = List.of(20240101,
                                20240102, 20240103, 20240104,
                                20240105, 20240108, 20240109,
                                20240110, 20240111, 20240112);
                TimeFrame timeFrame = TimeFrame.WEEKLY;
                List<Integer> result = DateConverter
                                .convertDates(dateList,
                                                timeFrame);
                List<Integer> expected = List.of(20240105,
                                20240112);

                assertEquals(expected, result,
                                "should return the latest day in the datelist of each week");

        }

        @Test
        @DisplayName("Daily to weekly conversion different years test")
        void convertToWeeklyTest2() {
                List<Integer> dateList = List.of(20231231,
                                20240101, 20240102, 20240103,
                                20240104, 20240105, 20240108,
                                20240109, 20240110, 20240111,
                                20240112);
                TimeFrame timeFrame = TimeFrame.WEEKLY;
                List<Integer> result = DateConverter
                                .convertDates(dateList,
                                                timeFrame);
                List<Integer> expected = List.of(20231231,
                                20240105, 20240112);

                assertEquals(expected, result,
                                "should return the latest day in the datelist of each week");
        }

        @Test
        @DisplayName("Daily to weekly conversion befor and after 2020 years")
        void convertToWeeklyTest3() {
                List<Integer> dateList = List.of(20190101,
                                20190202, 20200103, 20200204,
                                20200305, 20210108, 20220109,
                                20220210, 20230111, 20240112);
                TimeFrame timeFrame = TimeFrame.WEEKLY;
                List<Integer> result = DateConverter
                                .convertDates(dateList,
                                                timeFrame);
                List<Integer> expected = List.of(20190101,
                                20190202, 20200103, 20200204,
                                20200305, 20210108, 20220109,
                                20220210, 20230111, 20240112);
                assertEquals(expected, result,
                                "should return the latest day in the datelist of each week");
        }

        @Test
        @DisplayName("No dates")
        void convertToWeeklyTest4() {
                List<Integer> dateList = List.of();
                TimeFrame timeFrame = TimeFrame.WEEKLY;
                List<Integer> result = DateConverter
                                .convertDates(dateList,
                                                timeFrame);
                List<Integer> expected = List.of();
                assertEquals(expected, result,
                                "Should do nothing");
        }

        @Test
        @DisplayName("Daily to monthly conversion test")
        void convertToMonthlyTest() {
                List<Integer> dateList = List.of(20240101,
                                20240102, 20240103, 20240104,
                                20240105, 20240108, 20240109,
                                20240110, 20240111, 20240112,
                                20240201, 20240202, 20240301);
                TimeFrame timeFrame = TimeFrame.MONTHLY;
                List<Integer> result = DateConverter
                                .convertDates(dateList,
                                                timeFrame);
                List<Integer> expected = List.of(20240112,
                                20240202, 20240301);

                assertEquals(expected, result,
                                "should return the latest day in the datelist of each month");
        }

        @Test
        @DisplayName("Daily to monthly conversion test different years")
        void convertToMonthlyTest2() {
                List<Integer> dateList = List.of(20190102,
                                20200103, 20210104, 20220105,
                                20230108, 20240109, 20250110,
                                20260111, 20270112, 20280201,
                                20290202, 20300301);
                TimeFrame timeFrame = TimeFrame.MONTHLY;
                List<Integer> result = DateConverter
                                .convertDates(dateList,
                                                timeFrame);
                List<Integer> expected = List.of(20190102,
                                20200103, 20210104, 20220105,
                                20230108, 20240109, 20250110,
                                20260111, 20270112, 20280201,
                                20290202, 20300301);
                assertEquals(expected, result,
                                "should return the latest day in the datelist of each month");

        }
}
