package com.alerts.PasswordNotification.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    public static LocalDate parseDate(String date) {
        return LocalDate.parse(date, DATE_FORMATTER);
    }

    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    public static boolean isExactDaysBeforeRotation(LocalDate rotationDate, int days) {
        LocalDate targetDate = rotationDate.minusDays(days);
        return LocalDate.now().isEqual(targetDate);
    }
}