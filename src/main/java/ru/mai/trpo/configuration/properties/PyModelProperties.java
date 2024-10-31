package ru.mai.trpo.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ai-model.client")
public record PyModelProperties(String url) {
}
