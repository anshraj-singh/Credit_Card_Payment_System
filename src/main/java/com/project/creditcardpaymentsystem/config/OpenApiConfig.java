package com.project.creditcardpaymentsystem.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("Development Server (Localhost)");

        Server productionServer = new Server()
                .url("https://credit-card-payment-system.onrender.com")
                .description("Production Environment (Render Cloud)");

        return new OpenAPI()
                .servers(Arrays.asList(localServer, productionServer))
                .info(new Info()
                        .title("Credit Card Payment System")
                        .version("1.0.0")
                        .description("Comprehensive backend API documentation featuring Mongo DB connectivity, " +
                                "Stripe checkout integration, Gemini AI capabilities, and secure JWT infrastructure.")
                        .contact(new Contact()
                                .name("Credit Card System Support")
                                .email("support@ccpaymentsystem.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                // Link the security requirement globally to all endpoints
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Paste your valid JWT token obtained from the `/auth/login` endpoint to access secured routes.")));
    }
}