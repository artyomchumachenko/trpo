package ru.mai.trpo.rest;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import ru.mai.trpo.configuration.jwt.JwtAuthenticationFilter;
import ru.mai.trpo.configuration.jwt.JwtTokenProvider;
import ru.mai.trpo.configuration.jwt.service.CustomUserDetailsService;
import ru.mai.trpo.configuration.properties.OutsideRequestsProperties;
import ru.mai.trpo.model.User;
import ru.mai.trpo.service.UserService;

import static org.mockito.ArgumentMatchers.any;

@WebMvcTest
@Import(TestSecurityConfig.class) // Используем тестовую конфигурацию для Spring Security
public abstract class AbstractControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected JwtTokenProvider jwtTokenProvider;

    @MockBean
    protected CustomUserDetailsService customUserDetailsService;

    @MockBean
    protected OutsideRequestsProperties outsideRequestsProperties;

    @MockBean
    protected UserService userService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Настройка моков для работы с аутентификацией.
     * Вызывается в каждом тесте для корректной симуляции авторизованного пользователя.
     */
    @BeforeEach
    void setupAuthenticationMocks() {
        // Настраиваем мок JwtTokenProvider
        Mockito.when(jwtTokenProvider.validateToken(Mockito.anyString())).thenReturn(true);
        Mockito.when(jwtTokenProvider.getUsernameFromAuthorizationHeader(Mockito.anyString())).thenReturn("test_user");

        // Настраиваем мок CustomUserDetailsService
        Mockito.when(jwtTokenProvider.getUsernameFromJWT(any())).thenReturn("test_user");
        Mockito.when(customUserDetailsService.loadUserByUsername(Mockito.anyString())).thenReturn(
                new org.springframework.security.core.userdetails.User(
                        "test_user",
                        "test_password",
                        List.of() // Пустой список ролей
                )
        );

        // Настраиваем UserService
        User mockUser = new User();
        mockUser.setUsername("test_user");
        Mockito.when(userService.getUserByUsername("test_user")).thenReturn(mockUser);
    }
}

