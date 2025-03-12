package com.alerts.PasswordNotification.service;

import com.alerts.PasswordNotification.model.NotificationType;
import com.alerts.PasswordNotification.model.User;
import com.alerts.PasswordNotification.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final List<User> users = new ArrayList<>();

    @Autowired
    private EmailService emailService;

    @PostConstruct
    public void init() {
        // Initialize users with predefined data
        //LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate startDate = LocalDate.now().minusDays(165);


        // Collibra CDIP users
        users.add(new User("EMDO.DAPIS.User", "Collibra CDIP", "PAT",
                90, startDate, "collibra-support@example.com"));
        users.add(new User("EMDO.DAPIS.User", "Collibra CDIP", "PROD",
                90, startDate, "collibra-support@example.com"));

        // Oracle DB users
        users.add(new User("AEDMDA20ORC", "Oracle DB", "PAT",
                180, startDate, "oracle-db-support@example.com"));
        users.add(new User("PEDMDA984ORAC", "Oracle DB", "PROD",
                180, startDate, "oracle-db-support@example.com"));
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    @Override
    public void checkAndSendNotifications() {
        for (User user : users) {
            LocalDate nextRotationDate = user.getNextRotationDate();

            for (NotificationType type : NotificationType.values()) {
                if (DateUtils.isExactDaysBeforeRotation(nextRotationDate, type.getDaysBeforeRotation())) {
                    emailService.sendPasswordResetNotification(user, type);
                }
            }
        }
    }

    @Override
    public User addUser(User user) {
        users.add(user);
        return user;
    }

    @Override
    public User getUserByUsername(String username) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<User> getUsersByApplication(String application) {
        return users.stream()
                .filter(user -> user.getApplication().equals(application))
                .collect(Collectors.toList());
    }
}