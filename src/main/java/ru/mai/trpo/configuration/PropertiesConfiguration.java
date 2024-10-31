package ru.mai.trpo.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import ru.mai.trpo.configuration.properties.PyModelProperties;

@Configuration
@EnableConfigurationProperties(PyModelProperties.class)
public class PropertiesConfiguration {
}
