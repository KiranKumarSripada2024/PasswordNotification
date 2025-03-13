package com.alerts.PasswordNotification.controller;

import com.alerts.PasswordNotification.model.User;
import com.alerts.PasswordNotification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notification Controller", description = "Endpoints for managing password notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Value("${notification.first.days}")
    private int firstNotificationDays;

    @Value("${notification.second.days}")
    private int secondNotificationDays;

    @GetMapping("/users")
    @Operation(summary = "Get all users", description = "Returns all users in the current environment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved users")
    })
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(notificationService.getAllUsers());
    }

    @GetMapping("/users/{username}")
    @Operation(summary = "Get user by username", description = "Returns a user by their username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<User> getUserByUsername(
            @Parameter(description = "Username of the user to retrieve")
            @PathVariable String username) {
        User user = notificationService.getUserByUsername(username);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/users/application/{application}")
    @Operation(summary = "Get users by application", description = "Returns users filtered by application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved users")
    })
    public ResponseEntity<List<User>> getUsersByApplication(
            @Parameter(description = "Application name to filter users by")
            @PathVariable String application) {
        return ResponseEntity.ok(notificationService.getUsersByApplication(application));
    }

    @PostMapping("/check")
    @Operation(summary = "Manually check notifications", description = "Triggers notification check manually")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification check triggered successfully")
    })
    public ResponseEntity<Map<String, Object>> manualCheckNotifications() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Boolean> notificationsSent = notificationService.checkAndSendNotificationsWithResult();

        boolean anyNotificationsSent = notificationsSent.get("firstNotification") || notificationsSent.get("secondNotification");

        if (anyNotificationsSent) {
            result.put("status", "SUCCESS");
            result.put("message", "Notifications check complete - emails have been sent");

            if (notificationsSent.get("firstNotification")) {
                result.put("firstNotification", "Sent " + firstNotificationDays + "-day notifications");
            }

            if (notificationsSent.get("secondNotification")) {
                result.put("secondNotification", "Sent " + secondNotificationDays + "-day notifications");
            }
        } else {
            result.put("status", "INFO");
            result.put("message", "Checked for notifications but today is not a notification day for any user");
        }

        return ResponseEntity.ok(result);
    }
}