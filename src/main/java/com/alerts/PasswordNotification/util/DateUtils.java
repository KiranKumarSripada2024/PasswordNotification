package com.alerts.PasswordNotification.util;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    private static final DateTimeFormatter UTC_FORMATTER = DateTimeFormatter.ISO_ZONED_DATE_TIME;

    public static LocalDate parseDate(String date) {
        return LocalDate.parse(date, DATE_FORMATTER);
    }

    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    public static LocalDate getNowUtc() {
        return ZonedDateTime.now(ZoneOffset.UTC).toLocalDate();
    }

    public static LocalDate parseUtcDate(String utcDateString) {
        ZonedDateTime zdt = ZonedDateTime.parse(utcDateString, UTC_FORMATTER);
        return zdt.toLocalDate();
    }

    public static String formatToUtc(LocalDate date) {
        ZonedDateTime zdt = date.atStartOfDay(ZoneOffset.UTC);
        return zdt.format(UTC_FORMATTER);
    }
}