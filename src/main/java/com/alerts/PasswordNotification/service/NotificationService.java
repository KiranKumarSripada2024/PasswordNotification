package com.alerts.PasswordNotification.service;

import java.util.List;
import com.alerts.PasswordNotification.model.User;

public interface NotificationService {
    List<User> getAllUsers();
    void checkAndSendNotifications();
    User addUser(User user);
    User getUserByUsername(String username);
    List<User> getUsersByApplication(String application);
}