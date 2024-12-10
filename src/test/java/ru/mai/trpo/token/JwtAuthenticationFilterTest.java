package ru.mai.trpo.token;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import ru.mai.trpo.configuration.jwt.JwtAuthenticationFilter;
import ru.mai.trpo.configuration.jwt.JwtTokenProvider;
import ru.mai.trpo.configuration.jwt.service.CustomUserDetailsService;

import jakarta.servlet.ServletException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterTest {

    private JwtTokenProvider jwtTokenProvider;
    private CustomUserDetailsService userDetailsService;
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setup() {
        jwtTokenProvider = mock(JwtTokenProvider.class);
        userDetailsService = mock(CustomUserDetailsService.class);
        filter = new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);
    }

    @Test
    @DisplayName("Должен установить аутентификацию в контекст, если токен валидный")
    void shouldSetAuthenticationIfTokenIsValid() throws ServletException, IOException {
        // Подготовка данных
        String validToken = "valid_token";
        String username = "test_user";

        // Мокаем поведение jwtTokenProvider
        when(jwtTokenProvider.validateToken(validToken)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromJWT(validToken)).thenReturn(username);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getAuthorities()).thenReturn(new ArrayList<>());
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        // Создаем mock запрос/ответ/цепочку фильтров
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + validToken);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        // Выполняем фильтр
        filter.doFilterInternal(request, response, chain);

        // Проверяем, что аутентификация установлена
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getPrincipal()).isEqualTo(userDetails);
    }

    @Test
    @DisplayName("Должен возвращать 401, если токен отсутствует или невалиден")
    void shouldReturn401IfTokenInvalid() throws ServletException, IOException {
        // Мокаем невалидный токен
        String invalidToken = "invalid_token";
        when(jwtTokenProvider.validateToken(invalidToken)).thenReturn(false);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + invalidToken);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        // Выполняем фильтр
        filter.doFilterInternal(request, response, chain);

        // Проверяем, что статус 401
        assertThat(response.getStatus()).isEqualTo(401);
        // Проверяем, что контекст аутентификации не был установлен
        assertThat(org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Должен возвращать 401 если токен отсутствует")
    void shouldReturn401IfNoToken() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilterInternal(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

}
