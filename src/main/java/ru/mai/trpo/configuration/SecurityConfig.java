package ru.mai.trpo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import ru.mai.trpo.configuration.jwt.JwtAccessDeniedHandler;
import ru.mai.trpo.configuration.jwt.JwtAuthenticationEntryPoint;
import ru.mai.trpo.configuration.jwt.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

/**
 * Конфигурация системы безопасности приложения.
 * <p>
 * Класс отвечает за настройку Spring Security:
 * <ul>
 *     <li>Включение CORS</li>
 *     <li>Отключение CSRF</li>
 *     <li>Установка политики сессии "STATELESS"</li>
 *     <li>Настройка правил авторизации для различных запросов</li>
 *     <li>Добавление JWT-фильтра для аутентификации</li>
 *     <li>Определение точек входа при аутентификации/авторизации</li>
 * </ul>
 */
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    /**
     * Определяет цепочку фильтров безопасности.
     *
     * @param http объект типа {@link HttpSecurity} для настройки параметров безопасности
     * @return объект {@link SecurityFilterChain}, описывающий конфигурацию безопасности
     * @throws Exception в случае ошибок настройки
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configure(http))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/user/login", "/api/user/registration").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll())
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Определяет кодировщик паролей для хранения в зашифрованном виде.
     *
     * @return объект {@link PasswordEncoder}, реализующий шифрование паролей
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Создает менеджер аутентификации на основе конфигурации Spring Security.
     *
     * @param authenticationConfiguration объект конфигурации аутентификации
     * @return объект {@link AuthenticationManager} для проверки учетных данных пользователей
     * @throws Exception в случае ошибок инициализации менеджера аутентификации
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
