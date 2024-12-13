package ru.mai.trpo.configuration.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import ru.mai.trpo.configuration.jwt.service.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Фильтр для аутентификации пользователей на основе JWT.
 * <p>
 * Класс наследуется от {@link org.springframework.web.filter.OncePerRequestFilter}
 * и выполняется один раз для каждого запроса. Он извлекает JWT из заголовка Authorization,
 * валидирует его, а при успешной проверке устанавливает аутентификацию в контекст безопасности Spring.
 * В случае неуспеха возвращается код ошибки 401 (Unauthorized).
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    /**
     * Основной метод фильтра для обработки входящего запроса.
     *
     * @param request     HTTP-запрос
     * @param response    HTTP-ответ
     * @param filterChain цепочка последующих фильтров
     * @throws ServletException в случае ошибок сервлета
     * @throws IOException      в случае ошибок ввода/вывода
     */
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String jwt = getJwtFromRequest(request);

            if (jwt == null || !jwtTokenProvider.validateToken(jwt)) {
                throw new AuthenticationException("Токен отсутствует или недействителен") {};
            }

            setAuthenticationContext(jwt, request);
        } catch (AuthenticationException e) {
            SecurityContextHolder.clearContext();
            logger.error("Ошибка аутентификации: ", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            return;
        } catch (Exception e) {
            logger.error("Не удалось установить аутентификацию пользователя в контекст", e);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Извлекает JWT-токен из заголовка HTTP-запроса.
     *
     * @param request HTTP-запрос
     * @return строковое представление JWT-токена или {@code null}, если токен не найден
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * Устанавливает аутентификационную информацию в контекст безопасности.
     *
     * @param jwt     JWT-токен
     * @param request текущий HTTP-запрос
     */
    private void setAuthenticationContext(String jwt, HttpServletRequest request) {
        String username = jwtTokenProvider.getUsernameFromJWT(jwt);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
