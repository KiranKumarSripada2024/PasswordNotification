package com.alerts.PasswordNotification;

import com.alerts.PasswordNotification.model.User;
import com.alerts.PasswordNotification.service.EmailService;
import com.alerts.PasswordNotification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "notification.first.days=30",
        "notification.second.days=15",
        "application.environment=TEST"
})
@Import(IntegrationTests.TestConfiguration.class)
public class IntegrationTests {

    @Configuration
    static class TestConfiguration {
        @Bean
        @Primary
        public NotificationService notificationService() {
            return mock(NotificationService.class);
        }

        @Bean
        @Primary
        public EmailService emailService() {
            return mock(EmailService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmailService emailService;

    private User createTestUser(String username, String application) {
        return new User(
                username,
                application,
                "TEST",
                90,
                LocalDate.now().minusDays(60),
                "test@example.com"
        );
    }

    @Test
    public void testGetAllUsersEndpoint() throws Exception {
        List<User> users = Arrays.asList(
                createTestUser("user1", "App1"),
                createTestUser("user2", "App2")
        );
        when(notificationService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/notifications/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username", is("user1")))
                .andExpect(jsonPath("$[1].username", is("user2")));

        verify(notificationService, times(1)).getAllUsers();
    }

    @Test
    public void testGetUserByUsernameEndpoint_Found() throws Exception {
        User user = createTestUser("testUser", "TestApp");
        when(notificationService.getUserByUsername("testUser")).thenReturn(user);

        mockMvc.perform(get("/api/notifications/users/testUser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("testUser")))
                .andExpect(jsonPath("$.application", is("TestApp")));

        verify(notificationService, times(1)).getUserByUsername("testUser");
    }

    @Test
    public void testGetUserByUsernameEndpoint_NotFound() throws Exception {
        when(notificationService.getUserByUsername("nonexistent")).thenReturn(null);

        mockMvc.perform(get("/api/notifications/users/nonexistent")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(notificationService, times(1)).getUserByUsername("nonexistent");
    }

    @Test
    public void testGetUsersByApplicationEndpoint() throws Exception {
        List<User> users = Arrays.asList(
                createTestUser("user1", "TestApp"),
                createTestUser("user2", "TestApp")
        );
        when(notificationService.getUsersByApplication("TestApp")).thenReturn(users);

        mockMvc.perform(get("/api/notifications/users/application/TestApp")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username", is("user1")))
                .andExpect(jsonPath("$[1].username", is("user2")))
                .andExpect(jsonPath("$[0].application", is("TestApp")))
                .andExpect(jsonPath("$[1].application", is("TestApp")));

        verify(notificationService, times(1)).getUsersByApplication("TestApp");
    }

    @Test
    public void testManualCheckNotificationsEndpoint_WithNotifications() throws Exception {
        Map<String, Boolean> serviceResult = new HashMap<>();
        serviceResult.put("firstNotification", true);
        serviceResult.put("secondNotification", true);
        when(notificationService.checkAndSendNotificationsWithResult()).thenReturn(serviceResult);

        mockMvc.perform(post("/api/notifications/check")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("SUCCESS")))
                .andExpect(jsonPath("$.message", containsString("emails have been sent")))
                .andExpect(jsonPath("$.firstNotification").exists())
                .andExpect(jsonPath("$.secondNotification").exists());

        verify(notificationService, times(1)).checkAndSendNotificationsWithResult();
    }

    @Test
    public void testManualCheckNotificationsEndpoint_NoNotifications() throws Exception {
        Map<String, Boolean> serviceResult = new HashMap<>();
        serviceResult.put("firstNotification", false);
        serviceResult.put("secondNotification", false);
        when(notificationService.checkAndSendNotificationsWithResult()).thenReturn(serviceResult);

        mockMvc.perform(post("/api/notifications/check")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("INFO")))
                .andExpect(jsonPath("$.message", containsString("not a notification day")));

        verify(notificationService, times(1)).checkAndSendNotificationsWithResult();
    }
}