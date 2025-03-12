package com.alerts.PasswordNotification.controller;

import com.alerts.PasswordNotification.model.User;
import com.alerts.PasswordNotification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(notificationService.getAllUsers());
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = notificationService.getUserByUsername(username);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/users/application/{application}")
    public ResponseEntity<List<User>> getUsersByApplication(@PathVariable String application) {
        return ResponseEntity.ok(notificationService.getUsersByApplication(application));
    }

    @PostMapping("/check")
    public ResponseEntity<String> manualCheckNotifications() {
        notificationService.checkAndSendNotifications();
        return ResponseEntity.ok("Notification check triggered manually");
    }
}