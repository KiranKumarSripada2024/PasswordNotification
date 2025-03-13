package com.alerts.PasswordNotification;

import com.alerts.PasswordNotification.config.AppConfig;
import com.alerts.PasswordNotification.config.EmailConfig;
import com.alerts.PasswordNotification.config.SwaggerConfig;
import com.alerts.PasswordNotification.controller.NotificationController;
import com.alerts.PasswordNotification.scheduler.NotificationScheduler;
import com.alerts.PasswordNotification.service.EmailService;
import com.alerts.PasswordNotification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;


@SpringBootTest
@ActiveProfiles("test")
@Import(PasswordNotificationApplicationTests.TestConfiguration.class)
class PasswordNotificationApplicationTests {

	@Configuration
	static class TestConfiguration {
		@Bean
		@Primary
		public JavaMailSender javaMailSender() {
			return mock(JavaMailSender.class);
		}
	}

	@Autowired
	private PasswordNotificationApplication application;

	@Autowired
	private AppConfig appConfig;

	@Autowired
	private SwaggerConfig swaggerConfig;

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private EmailConfig emailConfig;

	@Autowired
	private NotificationController notificationController;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private NotificationScheduler notificationScheduler;

	@Test
	void contextLoads() {
		assertNotNull(application);
		assertNotNull(appConfig);
		assertNotNull(swaggerConfig);
		assertNotNull(emailConfig);
		assertNotNull(notificationController);
		assertNotNull(notificationService);
		assertNotNull(emailService);
		assertNotNull(notificationScheduler);
	}
}