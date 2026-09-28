package com.ait.app.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    // Inject base URL from application.properties or environment variable
    @Value("${app.openapi.server-url:/}")
    private String serverUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        // Allows Swagger "Try it out" to send requests to the correct server/domain
        Server activeServer = new Server()
                .url(serverUrl)
                .description("Deployed Server URL");

        return new OpenAPI()
                .servers(List.of(activeServer))
                .info(new Info()
                        .title("Food App REST API")
                        .version("1.0.0")
                        .description("Backend APIs for food ordering, restaurants, menu items, and deliveries.")
                        .contact(new Contact().name("Food App Dev Team").email("dev@yourfoodapp.com")))
                // Enable global authorization lock in Swagger UI
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
