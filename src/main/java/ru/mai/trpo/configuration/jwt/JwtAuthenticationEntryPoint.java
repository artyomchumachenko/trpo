package ru.mai.trpo.configuration.jwt;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * Обработчик неавторизованного доступа к ресурсу.
 * <p>
 * Класс реализует интерфейс {@link org.springframework.security.web.AuthenticationEntryPoint}
 * и вызывается при попытке доступа к защищенному ресурсу без корректной аутентификации.
 * В случае отсутствия или недействительного токена отправляет ответ с кодом состояния 401 (Unauthorized).
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    /**
     * Инициирует процесс ответа на запрос, когда пользователь не прошел аутентификацию.
     *
     * @param request  HTTP-запрос
     * @param response HTTP-ответ
     * @param authException исключение, содержащее причины неаутентифицированного доступа
     * @throws IOException при ошибках записи в ответ
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        logger.error("Невалидный токен или отсутствует токен. Причина: {}", authException.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Доступ запрещен: необходимо пройти аутентификацию.");
    }
}
