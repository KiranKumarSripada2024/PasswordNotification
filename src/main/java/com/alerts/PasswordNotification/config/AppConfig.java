package com.alerts.PasswordNotification.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application_${spring.profiles.active}.properties")
public class AppConfig {
}
