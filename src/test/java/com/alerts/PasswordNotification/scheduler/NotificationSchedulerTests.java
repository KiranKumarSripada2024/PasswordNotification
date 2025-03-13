package com.alerts.PasswordNotification.scheduler;

import com.alerts.PasswordNotification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationSchedulerTests {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationScheduler notificationScheduler;

    @Test
    public void testCheckPasswordRotations_WithNotifications() {
        Map<String, Boolean> serviceResult = new HashMap<>();
        serviceResult.put("firstNotification", true);
        serviceResult.put("secondNotification", false);
        when(notificationService.checkAndSendNotificationsWithResult()).thenReturn(serviceResult);

        notificationScheduler.checkPasswordRotations();

        verify(notificationService, times(1)).checkAndSendNotificationsWithResult();
    }

    @Test
    public void testCheckPasswordRotations_NoNotifications() {
        Map<String, Boolean> serviceResult = new HashMap<>();
        serviceResult.put("firstNotification", false);
        serviceResult.put("secondNotification", false);
        when(notificationService.checkAndSendNotificationsWithResult()).thenReturn(serviceResult);

        notificationScheduler.checkPasswordRotations();

        verify(notificationService, times(1)).checkAndSendNotificationsWithResult();
    }
}