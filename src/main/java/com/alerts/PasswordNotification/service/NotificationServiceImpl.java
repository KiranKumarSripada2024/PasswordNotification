package com.alerts.PasswordNotification.service;

import com.alerts.PasswordNotification.model.NotificationType;
import com.alerts.PasswordNotification.model.User;
import com.alerts.PasswordNotification.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private final List<User> users = new ArrayList<>();

    @Autowired
    private EmailService emailService;

    @Value("${application.environment}")
    private String currentEnvironment;

    @Value("${notification.first.days}")
    private int firstNotificationDays;

    @Value("${notification.second.days}")
    private int secondNotificationDays;

    @Value("${password.rotation.collibra}")
    private int collibraRotationFrequency;

    @Value("${password.rotation.oracle}")
    private int oracleRotationFrequency;

    @Value("${user.start.days.offset:-165}")
    private int startDaysOffset;

    @PostConstruct
    public void init() {
        // Calculate start date using the offset, in UTC
        LocalDate utcNow = DateUtils.getNowUtc();
        LocalDate startDate = utcNow.plusDays(startDaysOffset);

        logger.info("Initializing users with UTC start date: {}", startDate);
        logger.info("Current environment: {}", currentEnvironment);

        // Collibra CDIP users
        users.add(new User("EMDO.DAPIS.User", "Collibra CDIP", "PAT",
                collibraRotationFrequency, startDate, "collibra-support@example.com"));
        users.add(new User("EMDO.DAPIS.User", "Collibra CDIP", "PROD",
                collibraRotationFrequency, startDate, "collibra-support@example.com"));

        // Oracle DB users
        users.add(new User("AEDMDA20ORC", "Oracle DB", "PAT",
                oracleRotationFrequency, startDate, "oracle-db-support@example.com"));
        users.add(new User("PEDMDA984ORAC", "Oracle DB", "PROD",
                oracleRotationFrequency, startDate, "oracle-db-support@example.com"));

        for (User user : users) {
            logger.info("User: {}, Next rotation: {}, Days until rotation: {}",
                    user.getUsername(), user.getNextRotationDate(), user.getDaysUntilRotation());
        }
    }

    @Override
    public List<User> getAllUsers() {
        return users.stream()
                .filter(user -> user.getEnvironment().equals(currentEnvironment))
                .collect(Collectors.toList());
    }

    @Override
    public void checkAndSendNotifications() {
        logger.info("Running notification check using UTC time...");

        for (User user : users) {
            // Only process users for the current environment
            if (!user.getEnvironment().equals(currentEnvironment)) {
                logger.debug("Skipping user {} in environment {}", user.getUsername(), user.getEnvironment());
                continue;
            }

            LocalDate nextRotationDate = user.getNextRotationDate();

            // Calculate days until rotation using UTC
            LocalDate utcNow = DateUtils.getNowUtc();
            long daysUntil = java.time.temporal.ChronoUnit.DAYS.between(utcNow, nextRotationDate);

            logger.info("Checking user: {}, Next rotation: {}, Days until rotation: {} (UTC)",
                    user.getUsername(), nextRotationDate, daysUntil);

            // Send first notification exactly when days until rotation equals firstNotificationDays
            if (daysUntil == firstNotificationDays) {
                logger.info("Sending first notification for user: {}", user.getUsername());
                emailService.sendPasswordResetNotification(user, NotificationType.FIRST_NOTIFICATION);
            }

            // Send second notification exactly when days until rotation equals secondNotificationDays
            if (daysUntil == secondNotificationDays) {
                logger.info("Sending second notification for user: {}", user.getUsername());
                emailService.sendPasswordResetNotification(user, NotificationType.SECOND_NOTIFICATION);
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
                .filter(user -> user.getUsername().equals(username) &&
                        user.getEnvironment().equals(currentEnvironment))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<User> getUsersByApplication(String application) {
        return users.stream()
                .filter(user -> user.getApplication().equals(application) &&
                        user.getEnvironment().equals(currentEnvironment))
                .collect(Collectors.toList());
    }
}