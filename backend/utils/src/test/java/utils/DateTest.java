package utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DateTest {

    @Test
    @DisplayName("Test day of week")
    void dayOfWeekTest() {
        assertEquals(1, Date.extractDayOfWeek(20240101));
        assertEquals(2, Date.extractDayOfWeek(20240102));
        assertEquals(3, Date.extractDayOfWeek(20240103));
        assertEquals(4, Date.extractDayOfWeek(20240104));
        assertEquals(5, Date.extractDayOfWeek(20240105));
        assertEquals(6, Date.extractDayOfWeek(20240106));
        assertEquals(7, Date.extractDayOfWeek(20240107));
    }

}
