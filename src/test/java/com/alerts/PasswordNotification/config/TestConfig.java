package com.alerts.PasswordNotification.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@TestConfiguration
@Profile("test")
public class TestConfig {

    @Bean
    @Primary
    public JavaMailSender javaMailSenderMock() {
        return new JavaMailSenderImpl();
    }
}