package com.alerts.PasswordNotification.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI passwordNotificationOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Password Notification API")
                        .description("Spring Boot REST API for Password Expiration Notifications")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Support Team")
                                .email("support@example.com")));
    }
}