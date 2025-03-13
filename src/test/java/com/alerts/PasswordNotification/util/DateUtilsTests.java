package com.alerts.PasswordNotification.util;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import static org.junit.jupiter.api.Assertions.*;

public class DateUtilsTests {

    @Test
    public void testParseDate() {
        String dateString = "15-Mar-2023";
        LocalDate result = DateUtils.parseDate(dateString);
        assertEquals(LocalDate.of(2023, 3, 15), result);
    }

    @Test
    public void testParseDate_InvalidFormat() {
        String invalidDateString = "2023-03-15";
        assertThrows(DateTimeParseException.class, () -> {
            DateUtils.parseDate(invalidDateString);
        });
    }

    @Test
    public void testFormatDate() {
        LocalDate date = LocalDate.of(2023, 3, 15);
        String result = DateUtils.formatDate(date);
        assertEquals("15-Mar-2023", result);
    }

    @Test
    public void testGetNowUtc() {
        LocalDate nowFromZone = ZonedDateTime.now(ZoneOffset.UTC).toLocalDate();
        LocalDate result = DateUtils.getNowUtc();
        assertEquals(nowFromZone, result);
    }

    @Test
    public void testParseUtcDate() {
        ZonedDateTime zdt = ZonedDateTime.of(2023, 3, 15, 12, 0, 0, 0, ZoneOffset.UTC);
        String utcString = zdt.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
        LocalDate result = DateUtils.parseUtcDate(utcString);
        assertEquals(LocalDate.of(2023, 3, 15), result);
    }

    @Test
    public void testFormatToUtc() {
        LocalDate date = LocalDate.of(2023, 3, 15);
        String result = DateUtils.formatToUtc(date);
        assertTrue(result.contains("2023-03-15T00:00:00Z"));
    }

    @Test
    public void testRoundTripConversion() {
        LocalDate originalDate = LocalDate.of(2023, 3, 15);
        String utcString = DateUtils.formatToUtc(originalDate);
        LocalDate parsedDate = DateUtils.parseUtcDate(utcString);
        assertEquals(originalDate, parsedDate);
    }

    @Test
    public void testParseUtcDate_InvalidFormat() {
        String invalidUtcString = "2023-03-15";
        assertThrows(DateTimeParseException.class, () -> {
            DateUtils.parseUtcDate(invalidUtcString);
        });
    }
}