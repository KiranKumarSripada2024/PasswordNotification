package com.alerts.PasswordNotification.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port}")
    private String serverPort;

    @Value("${application.environment}")
    private String environment;

    @Bean
    public OpenAPI passwordNotificationOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Password Notification API")
                        .description("Spring Boot REST API for Password Expiration Notifications")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Support Team")
                                .email("support@example.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description(environment + " Server")
                ));
    }
}