package com.alerts.PasswordNotification.controller;

import com.alerts.PasswordNotification.model.User;
import com.alerts.PasswordNotification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationControllerTests {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    private User testUser1;
    private User testUser2;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(notificationController, "firstNotificationDays", 30);
        ReflectionTestUtils.setField(notificationController, "secondNotificationDays", 15);

        testUser1 = new User(
                "testUser1",
                "TestApp1",
                "TEST",
                90,
                LocalDate.now().minusDays(60),
                "support1@example.com"
        );

        testUser2 = new User(
                "testUser2",
                "TestApp2",
                "TEST",
                180,
                LocalDate.now().minusDays(30),
                "support2@example.com"
        );
    }

    @Test
    public void testGetAllUsers() {
        when(notificationService.getAllUsers()).thenReturn(Arrays.asList(testUser1, testUser2));

        ResponseEntity<List<User>> response = notificationController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().contains(testUser1));
        assertTrue(response.getBody().contains(testUser2));

        verify(notificationService, times(1)).getAllUsers();
    }

    @Test
    public void testGetUserByUsername_Found() {
        when(notificationService.getUserByUsername("testUser1")).thenReturn(testUser1);

        ResponseEntity<User> response = notificationController.getUserByUsername("testUser1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUser1, response.getBody());

        verify(notificationService, times(1)).getUserByUsername("testUser1");
    }

    @Test
    public void testGetUserByUsername_NotFound() {
        when(notificationService.getUserByUsername("nonexistent")).thenReturn(null);

        ResponseEntity<User> response = notificationController.getUserByUsername("nonexistent");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(notificationService, times(1)).getUserByUsername("nonexistent");
    }

    @Test
    public void testGetUsersByApplication() {
        when(notificationService.getUsersByApplication("TestApp1")).thenReturn(Arrays.asList(testUser1));

        ResponseEntity<List<User>> response = notificationController.getUsersByApplication("TestApp1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(testUser1, response.getBody().get(0));

        verify(notificationService, times(1)).getUsersByApplication("TestApp1");
    }

    @Test
    public void testManualCheckNotifications_WithNotifications() {
        Map<String, Boolean> serviceResult = new HashMap<>();
        serviceResult.put("firstNotification", true);
        serviceResult.put("secondNotification", true);
        when(notificationService.checkAndSendNotificationsWithResult()).thenReturn(serviceResult);

        ResponseEntity<Map<String, Object>> response = notificationController.manualCheckNotifications();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertEquals("SUCCESS", body.get("status"));
        assertTrue(body.get("message").toString().contains("emails have been sent"));
        assertNotNull(body.get("firstNotification"));
        assertNotNull(body.get("secondNotification"));

        verify(notificationService, times(1)).checkAndSendNotificationsWithResult();
    }

    @Test
    public void testManualCheckNotifications_NoNotifications() {
        Map<String, Boolean> serviceResult = new HashMap<>();
        serviceResult.put("firstNotification", false);
        serviceResult.put("secondNotification", false);
        when(notificationService.checkAndSendNotificationsWithResult()).thenReturn(serviceResult);

        ResponseEntity<Map<String, Object>> response = notificationController.manualCheckNotifications();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertEquals("INFO", body.get("status"));
        assertTrue(body.get("message").toString().contains("not a notification day"));

        verify(notificationService, times(1)).checkAndSendNotificationsWithResult();
    }

    @Test
    public void testManualCheckNotifications_OnlyFirstNotification() {
        Map<String, Boolean> serviceResult = new HashMap<>();
        serviceResult.put("firstNotification", true);
        serviceResult.put("secondNotification", false);
        when(notificationService.checkAndSendNotificationsWithResult()).thenReturn(serviceResult);

        ResponseEntity<Map<String, Object>> response = notificationController.manualCheckNotifications();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertEquals("SUCCESS", body.get("status"));
        assertNotNull(body.get("firstNotification"));
        assertFalse(body.containsKey("secondNotification"));

        verify(notificationService, times(1)).checkAndSendNotificationsWithResult();
    }
}