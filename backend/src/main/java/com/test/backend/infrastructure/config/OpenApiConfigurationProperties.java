package com.test.backend.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "openapi")
public class OpenApiConfigurationProperties {
    private String title;
    private String description;
    private String version;
    private String url;
}
