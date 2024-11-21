package ru.mai.trpo.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import ru.mai.trpo.configuration.properties.OutsideRequestsProperties;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final OutsideRequestsProperties properties;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(properties.front()) // Разрешаем запросы с фронтенда
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Включите OPTIONS для предварительных запросов CORS
                .allowedHeaders("*") // Разрешаем все заголовки, включая Authorization
                .allowCredentials(true); // Если используете cookies или сессионные данные
    }
}
