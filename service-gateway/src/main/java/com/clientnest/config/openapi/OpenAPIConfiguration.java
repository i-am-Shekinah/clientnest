package com.clientnest.config.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
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

        return new OpenAPI()
                .info(info);
    }
}
