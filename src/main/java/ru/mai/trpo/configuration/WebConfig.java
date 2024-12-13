package ru.mai.trpo.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import ru.mai.trpo.configuration.properties.OutsideRequestsProperties;

import lombok.RequiredArgsConstructor;

/**
 * Конфигурация веб-среды, включая настройку CORS.
 * <p>
 * Класс настраивает разрешенные источники, методы и заголовки для междоменных запросов (CORS),
 * а также определяет возможность использования учетных данных (cookies).
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final OutsideRequestsProperties properties;

    /**
     * Настраивает правила CORS для всех путей приложения.
     *
     * @param registry реестр CORS-настроек
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(properties.front())
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
