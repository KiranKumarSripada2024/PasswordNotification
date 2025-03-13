package com.alerts.PasswordNotification.service;

import java.util.List;
import java.util.Map;
import com.alerts.PasswordNotification.model.User;

public interface NotificationService {
    List<User> getAllUsers();
    void checkAndSendNotifications();
    Map<String, Boolean> checkAndSendNotificationsWithResult();
    User addUser(User user);
    User getUserByUsername(String username);
    List<User> getUsersByApplication(String application);
}