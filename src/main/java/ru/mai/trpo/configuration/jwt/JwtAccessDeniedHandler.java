package ru.mai.trpo.configuration.jwt;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * Обработчик ситуации, когда доступ к ресурсу запрещен.
 * <p>
 * Класс реализует интерфейс {@link org.springframework.security.web.access.AccessDeniedHandler}
 * и вызывается при попытке неавторизованного доступа к защищенному ресурсу пользователем,
 * который прошел аутентификацию, но не имеет достаточных прав (ролевых привилегий).
 * В случае отсутствия необходимых прав отправляет ответ с кодом состояния 403 (Forbidden).
 */
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger logger = LoggerFactory.getLogger(JwtAccessDeniedHandler.class);

    /**
     * Обработчик события отказа в доступе.
     *
     * @param request  HTTP-запрос
     * @param response HTTP-ответ
     * @param accessDeniedException исключение, содержащее детали отсутствия доступа
     * @throws IOException при ошибках записи в ответ
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        logger.error("Доступ к ресурсу запрещен. Причина: {}", accessDeniedException.getMessage());
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Доступ запрещен: недостаточно прав для доступа к этому ресурсу.");
    }
}
