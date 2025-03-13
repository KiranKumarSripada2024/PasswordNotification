package com.alerts.PasswordNotification;

import com.alerts.PasswordNotification.model.User;
import com.alerts.PasswordNotification.util.DateUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {
        "notification.first.days=30",
        "notification.second.days=15",
        "password.rotation.collibra=90",
        "password.rotation.oracle=180",
        "user.start.days.offset=-60",
        "application.environment=TEST"
})
public class PasswordNotificationTests {
    private User createTestUser(String username, String application, String environment,
                                int rotationFrequency, LocalDate startDate) {
        return new User(
                username,
                application,
                environment,
                rotationFrequency,
                startDate,
                "test@example.com"
        );
    }

    @Test
    public void testUserModel() {
        LocalDate startDate = LocalDate.now().minusDays(30);
        User user = createTestUser("testUser", "TestApp", "TEST", 90, startDate);

        assertEquals("testUser", user.getUsername());
        assertEquals("TestApp", user.getApplication());
        assertEquals("TEST", user.getEnvironment());
        assertEquals(90, user.getPasswordRotationFrequency());
        assertEquals(startDate, user.getStartDate());
        assertEquals("test@example.com", user.getSupportTeamEmail());

        LocalDate expectedNextRotation = startDate.plusDays(90);
        assertEquals(expectedNextRotation, user.getNextRotationDate());

        int expectedDaysUntil = (int) java.time.temporal.ChronoUnit.DAYS.between(
                DateUtils.getNowUtc(), expectedNextRotation);
        assertEquals(expectedDaysUntil, user.getDaysUntilRotation());
    }

    @Test
    public void testUserNextRotationCalculation() {
        LocalDate startDate = LocalDate.now().minusDays(200);
        User user = createTestUser("testUser", "TestApp", "TEST", 90, startDate);

        LocalDate nextRotation = user.getNextRotationDate();
        assertTrue(nextRotation.isAfter(DateUtils.getNowUtc()));

        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, nextRotation);
        assertEquals(0, daysBetween % 90);
    }


    @Test
    public void testDateUtilsParsing() {
        String dateStr = "21-Mar-2023";
        LocalDate parsed = DateUtils.parseDate(dateStr);
        assertEquals(LocalDate.of(2023, 3, 21), parsed);

        String formatted = DateUtils.formatDate(parsed);
        assertEquals(dateStr, formatted);
    }

    @Test
    public void testDateUtilsUTC() {
        LocalDate utcNow = DateUtils.getNowUtc();
        assertNotNull(utcNow);

        String utcFormatted = DateUtils.formatToUtc(utcNow);
        assertTrue(utcFormatted.contains("Z")); // UTC time ends with Z

        LocalDate parsedBack = DateUtils.parseUtcDate(utcFormatted);
        assertEquals(utcNow, parsedBack);
    }
}