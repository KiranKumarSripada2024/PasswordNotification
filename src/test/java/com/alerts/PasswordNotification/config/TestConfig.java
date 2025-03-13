package com.alerts.PasswordNotification.config;

import com.alerts.PasswordNotification.service.EmailService;
import com.alerts.PasswordNotification.service.NotificationService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import static org.mockito.Mockito.mock;

@TestConfiguration
@Profile("test")
public class TestConfig {

    @Bean
    @Primary
    public JavaMailSender javaMailSenderMock() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("localhost");
        mailSender.setPort(3025);
        return mailSender;
    }

    @Bean
    @Primary
    public NotificationService notificationServiceMock() {
        return mock(NotificationService.class);
    }

    @Bean
    @Primary
    public EmailService emailServiceMock() {
        return mock(EmailService.class);
    }
}