package com.alerts.PasswordNotification.model;

import com.alerts.PasswordNotification.util.DateUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserTests {

    @Test
    public void testUserCreation() {
        String username = "testUser";
        String application = "TestApp";
        String environment = "TEST";
        int passwordRotationFrequency = 90;
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        String supportTeamEmail = "support@example.com";
        User user = new User(username, application, environment,
                passwordRotationFrequency, startDate, supportTeamEmail);

        assertEquals(username, user.getUsername());
        assertEquals(application, user.getApplication());
        assertEquals(environment, user.getEnvironment());
        assertEquals(passwordRotationFrequency, user.getPasswordRotationFrequency());
        assertEquals(startDate, user.getStartDate());
        assertEquals(supportTeamEmail, user.getSupportTeamEmail());
    }

    @Test
    public void testDefaultConstructorAndSetters() {
        User user = new User();
        String username = "testUser";
        String application = "TestApp";
        String environment = "TEST";
        int passwordRotationFrequency = 90;
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        String supportTeamEmail = "support@example.com";

        user.setUsername(username);
        user.setApplication(application);
        user.setEnvironment(environment);
        user.setPasswordRotationFrequency(passwordRotationFrequency);
        user.setStartDate(startDate);
        user.setSupportTeamEmail(supportTeamEmail);

        assertEquals(username, user.getUsername());
        assertEquals(application, user.getApplication());
        assertEquals(environment, user.getEnvironment());
        assertEquals(passwordRotationFrequency, user.getPasswordRotationFrequency());
        assertEquals(startDate, user.getStartDate());
        assertEquals(supportTeamEmail, user.getSupportTeamEmail());
    }

    @Test
    public void testGetNextRotationDate_SingleRotation() {
        LocalDate now = LocalDate.of(2023, 3, 15);
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        int rotationFrequency = 90; // 90 days

        try (MockedStatic<DateUtils> dateUtils = Mockito.mockStatic(DateUtils.class)) {
            dateUtils.when(DateUtils::getNowUtc).thenReturn(now);

            User user = new User("testUser", "TestApp", "TEST",
                    rotationFrequency, startDate, "support@example.com");

            LocalDate nextRotationDate = user.getNextRotationDate();
            assertEquals(LocalDate.of(2023, 4, 1), nextRotationDate);
        }
    }

    @Test
    public void testGetNextRotationDate_MultipleRotations() {
        LocalDate now = LocalDate.of(2023, 8, 15);
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        int rotationFrequency = 90;

        try (MockedStatic<DateUtils> dateUtils = Mockito.mockStatic(DateUtils.class)) {
            dateUtils.when(DateUtils::getNowUtc).thenReturn(now);

            User user = new User("testUser", "TestApp", "TEST",
                    rotationFrequency, startDate, "support@example.com");

            LocalDate nextRotationDate = user.getNextRotationDate();
            assertEquals(LocalDate.of(2023, 9, 28), nextRotationDate);
        }
    }

    @Test
    public void testGetNextRotationDate_ExactlyOnRotationDay() {
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        int rotationFrequency = 90;
        LocalDate now = LocalDate.of(2023, 4, 1);

        try (MockedStatic<DateUtils> dateUtils = Mockito.mockStatic(DateUtils.class)) {
            dateUtils.when(DateUtils::getNowUtc).thenReturn(now);

            User user = new User("testUser", "TestApp", "TEST",
                    rotationFrequency, startDate, "support@example.com");
            LocalDate nextRotationDate = user.getNextRotationDate();
            assertEquals(LocalDate.of(2023, 6, 30), nextRotationDate);
        }
    }

    @Test
    public void testGetDaysUntilRotation() {
        LocalDate now = LocalDate.of(2023, 3, 15);
        LocalDate nextRotation = LocalDate.of(2023, 4, 1);

        try (MockedStatic<DateUtils> dateUtils = Mockito.mockStatic(DateUtils.class)) {
            dateUtils.when(DateUtils::getNowUtc).thenReturn(now);

            User user = new User();
            User userTest = new User("testUser", "TestApp", "TEST",
                    90, LocalDate.of(2023, 1, 1), "support@example.com") {
                @Override
                public LocalDate getNextRotationDate() {
                    return nextRotation;
                }
            };
            int daysUntil = userTest.getDaysUntilRotation();
            assertEquals(17, daysUntil);
        }
    }

    @Test
    public void testToString() {
        String username = "testUser";
        String application = "TestApp";
        String environment = "TEST";
        int passwordRotationFrequency = 90;
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        String supportTeamEmail = "support@example.com";

        User user = new User(username, application, environment,
                passwordRotationFrequency, startDate, supportTeamEmail);
        String toString = user.toString();
        assertTrue(toString.contains(username));
        assertTrue(toString.contains(application));
        assertTrue(toString.contains(environment));
        assertTrue(toString.contains(String.valueOf(passwordRotationFrequency)));
        assertTrue(toString.contains(startDate.toString()));
        assertTrue(toString.contains(supportTeamEmail));
    }
}