package com.clientnest.config.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfiguration {

    @Bean
    public OpenAPI clientNestOpenAPI() {
        Contact contact = new Contact();
        contact.setEmail("michaelolatunji.dev@gmail.com");
        contact.setName("Michael Olatunji");

        Info info = new Info()
                .title("ClientNest API")
                .version("1.0")
                .contact(contact)
                .description("API documentation for ClientNest - Appointment & Client Management System");

        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(info)
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }
}
