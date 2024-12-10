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

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

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
            SecurityContextHolder.clearContext(); // Очистка контекста безопасности
            logger.error("Ошибка аутентификации: ", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            return; // Прекращаем выполнение фильтров
        } catch (Exception e) {
            logger.error("Не удалось установить аутентификацию пользователя в контекст", e);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Убираем "Bearer " из начала токена
        }
        return null;
    }

    private void setAuthenticationContext(String jwt, HttpServletRequest request) {
        String username = jwtTokenProvider.getUsernameFromJWT(jwt);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
