package ru.mai.trpo.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import ru.mai.trpo.configuration.properties.OutsideRequestsProperties;
import ru.mai.trpo.configuration.properties.PyModelProperties;

@Configuration
@EnableConfigurationProperties({PyModelProperties.class, OutsideRequestsProperties.class})
public class PropertiesConfiguration {
}
