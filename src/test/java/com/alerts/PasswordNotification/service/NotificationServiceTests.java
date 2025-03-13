package com.alerts.PasswordNotification.service;

import com.alerts.PasswordNotification.model.NotificationType;
import com.alerts.PasswordNotification.model.User;
import com.alerts.PasswordNotification.util.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTests {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private NotificationServiceImpl notificationService;
    private List<User> testUsers;
    private final String currentEnvironment = "TEST";

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(notificationService, "currentEnvironment", currentEnvironment);
        ReflectionTestUtils.setField(notificationService, "firstNotificationDays", 30);
        ReflectionTestUtils.setField(notificationService, "secondNotificationDays", 15);
        ReflectionTestUtils.setField(notificationService, "collibraRotationFrequency", 90);
        ReflectionTestUtils.setField(notificationService, "oracleRotationFrequency", 180);
        ReflectionTestUtils.setField(notificationService, "startDaysOffset", -60);

        testUsers = new ArrayList<>();

        testUsers.add(createTestUser("user1", "Collibra CDIP", currentEnvironment,
                30, DateUtils.getNowUtc().plusDays(30))); // Exactly at first notification

        testUsers.add(createTestUser("user2", "Oracle DB", currentEnvironment,
                30, DateUtils.getNowUtc().plusDays(15))); // Exactly at second notification

        testUsers.add(createTestUser("user3", "Collibra CDIP", currentEnvironment,
                30, DateUtils.getNowUtc().plusDays(5))); // Not at notification point

        testUsers.add(createTestUser("user4", "Collibra CDIP", "OTHERENV",
                30, DateUtils.getNowUtc().plusDays(30))); // Should be ignored

        ReflectionTestUtils.setField(notificationService, "users", testUsers);
    }

    private User createTestUser(String username, String application, String environment,
                                int rotationFrequency, LocalDate nextRotationDate) {
        LocalDate startDate = nextRotationDate.minusDays(rotationFrequency);

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
    public void testGetAllUsers() {
        List<User> users = notificationService.getAllUsers();

        assertEquals(3, users.size());
        for (User user : users) {
            assertEquals(currentEnvironment, user.getEnvironment());
        }
    }

    @Test
    public void testGetUserByUsername() {
        User user = notificationService.getUserByUsername("user1");
        assertNotNull(user);
        assertEquals("user1", user.getUsername());
        assertEquals(currentEnvironment, user.getEnvironment());

        User nonExistent = notificationService.getUserByUsername("nonexistent");
        assertNull(nonExistent);

        User wrongEnv = notificationService.getUserByUsername("user4");
        assertNull(wrongEnv);
    }

    @Test
    public void testGetUsersByApplication() {
        List<User> collibraUsers = notificationService.getUsersByApplication("Collibra CDIP");
        assertEquals(2, collibraUsers.size());
        for (User user : collibraUsers) {
            assertEquals("Collibra CDIP", user.getApplication());
            assertEquals(currentEnvironment, user.getEnvironment());
        }

        List<User> oracleUsers = notificationService.getUsersByApplication("Oracle DB");
        assertEquals(1, oracleUsers.size());
        assertEquals("Oracle DB", oracleUsers.get(0).getApplication());
    }

    @Test
    public void testCheckAndSendNotifications() {
        Map<String, Boolean> result = notificationService.checkAndSendNotificationsWithResult();

        verify(emailService, times(1))
                .sendPasswordResetNotification(
                        argThat(user -> user.getUsername().equals("user1")),
                        eq(NotificationType.FIRST_NOTIFICATION));

        verify(emailService, times(1))
                .sendPasswordResetNotification(
                        argThat(user -> user.getUsername().equals("user2")),
                        eq(NotificationType.SECOND_NOTIFICATION));

        verify(emailService, never())
                .sendPasswordResetNotification(
                        argThat(user -> user.getUsername().equals("user3")),
                        any(NotificationType.class));

        verify(emailService, never())
                .sendPasswordResetNotification(
                        argThat(user -> user.getUsername().equals("user4")),
                        any(NotificationType.class));

        assertTrue(result.get("firstNotification"));
        assertTrue(result.get("secondNotification"));
    }

    @Test
    public void testNoNotificationsSent() {
        testUsers.clear();
        testUsers.add(createTestUser("user1", "Collibra CDIP", currentEnvironment,
                30, DateUtils.getNowUtc().plusDays(20))); // Not at notification point
        testUsers.add(createTestUser("user2", "Oracle DB", currentEnvironment,
                30, DateUtils.getNowUtc().plusDays(10))); // Not at notification point

        Map<String, Boolean> result = notificationService.checkAndSendNotificationsWithResult();

        verify(emailService, never()).sendPasswordResetNotification(any(), any());

        assertFalse(result.get("firstNotification"));
        assertFalse(result.get("secondNotification"));
    }

    @Test
    public void testAddUser() {
        User newUser = createTestUser("newUser", "NewApp", currentEnvironment,
                30, DateUtils.getNowUtc().plusDays(10));

        User added = notificationService.addUser(newUser);
        assertEquals(newUser, added);

        User retrieved = notificationService.getUserByUsername("newUser");
        assertNotNull(retrieved);
        assertEquals("newUser", retrieved.getUsername());
    }
}