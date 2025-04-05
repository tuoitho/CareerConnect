package com.careerconnect.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${app.api.dev-url:http://localhost:8080}")
    private String devUrl;

    @Value("${app.api.prod-url:https://careerconnect-api.com}")
    private String prodUrl;

    @Bean
    public OpenAPI careerConnectOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Server URL in Development environment");

        Server prodServer = new Server();
        prodServer.setUrl(prodUrl);
        prodServer.setDescription("Server URL in Production environment");

        Contact contact = new Contact()
                .name("CareerConnect Team")
                .email("support@careerconnect.com")
                .url("https://careerconnect.com/contact");

        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("CareerConnect API Documentation")
                .description("This documentation provides details about CareerConnect RESTful APIs")
                .version("1.0.0")
                .contact(contact)
                .license(license)
                .termsOfService("https://careerconnect.com/terms");

        return new OpenAPI()
                .info(info)
                .externalDocs(new ExternalDocumentation()
                        .description("CareerConnect Wiki Documentation")
                        .url("https://careerconnect.com/docs"))
                .servers(List.of(devServer, prodServer));
    }
}