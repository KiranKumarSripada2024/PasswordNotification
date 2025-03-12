package com.alerts.PasswordNotification.service;

import com.alerts.PasswordNotification.model.NotificationType;
import com.alerts.PasswordNotification.model.User;

public interface EmailService {
    void sendPasswordResetNotification(User user, NotificationType notificationType);
}