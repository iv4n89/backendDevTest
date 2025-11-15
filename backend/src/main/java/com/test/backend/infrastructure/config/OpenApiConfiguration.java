package com.test.backend.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@OpenAPIDefinition
@RequiredArgsConstructor
@Configuration
public class OpenApiConfiguration {
    private final OpenApiConfigurationProperties openApiConfigurationProperties;

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .servers(List.of(new Server().url(openApiConfigurationProperties.getUrl())))
                .info(new Info()
                        .title(openApiConfigurationProperties.getTitle())
                        .version(openApiConfigurationProperties.getVersion())
                        .description(openApiConfigurationProperties.getDescription()));
    }
}