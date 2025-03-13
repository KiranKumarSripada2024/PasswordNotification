package com.alerts.PasswordNotification.scheduler;

import com.alerts.PasswordNotification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@EnableScheduling
public class NotificationScheduler {

    private static final Logger logger = LoggerFactory.getLogger(NotificationScheduler.class);

    @Autowired
    private NotificationService notificationService;

    // Run every day at midnight
    @Scheduled(cron = "0 0 0 * * ?")
    public void checkPasswordRotations() {
        logger.info("Running daily password rotation check");
        Map<String, Boolean> result = notificationService.checkAndSendNotificationsWithResult();

        if (result.get("firstNotification") || result.get("secondNotification")) {
            logger.info("Daily check complete - notifications were sent");
        } else {
            logger.info("Daily check complete - no notifications were sent for today");
        }
    }
}