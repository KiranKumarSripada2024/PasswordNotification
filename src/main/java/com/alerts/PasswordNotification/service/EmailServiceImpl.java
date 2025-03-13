package com.alerts.PasswordNotification.service;

import com.alerts.PasswordNotification.model.NotificationType;
import com.alerts.PasswordNotification.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Value("${notification.first.days}")
    private int firstNotificationDays;

    @Value("${notification.second.days}")
    private int secondNotificationDays;

    @Override
    public void sendPasswordResetNotification(User user, NotificationType notificationType) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(senderEmail);
            helper.setTo(user.getSupportTeamEmail());
            helper.setSubject(createSubject(user));
            helper.setText(createEmailBody(user, notificationType));

            emailSender.send(message);

            int daysBeforeRotation = (notificationType == NotificationType.FIRST_NOTIFICATION) ?
                    firstNotificationDays : secondNotificationDays;

            logger.info("Password reset notification sent for user: {} in environment: {}, {} days before rotation",
                    user.getUsername(), user.getEnvironment(), daysBeforeRotation);
        } catch (MessagingException e) {
            logger.error("Failed to send email notification", e);
        }
    }

    private String createSubject(User user) {
        if (user.getApplication().equals("Collibra CDIP")) {
            return "EMDO-Collibra-CDIP-User Account(" + user.getUsername() + ") Password Reset";
        } else {
            return user.getApplication() + " Account(" + user.getUsername() + ") Password Reset";
        }
    }

    private String createEmailBody(User user, NotificationType notificationType) {
        String supportTeam = user.getApplication().equals("Collibra CDIP") ?
                "Collibra L2 Support Team" : "Oracle DB Support Team";

        int daysBeforeRotation = (notificationType == NotificationType.FIRST_NOTIFICATION) ?
                firstNotificationDays : secondNotificationDays;

        return "Hi " + supportTeam + ",\n\n" +
                "This is a remainder to inform you that User account\n" +
                "<<" + user.getUsername() + ">>password needs to be reset in next " +
                daysBeforeRotation + " days.\n" +
                "<<Application Name>>: " + user.getApplication() + "\n" +
                "<<User Account>>: " + user.getUsername() + "\n" +
                "<<Environment>>: " + user.getEnvironment();
    }
}