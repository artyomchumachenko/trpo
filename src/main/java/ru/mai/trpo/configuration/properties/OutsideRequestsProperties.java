package ru.mai.trpo.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "outside-requests")
public record OutsideRequestsProperties(String front) {
}
