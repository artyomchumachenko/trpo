package ru.mai.trpo.token;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import ru.mai.trpo.configuration.jwt.JwtTokenProvider;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        // Устанавливаем значения полей через ReflectionTestUtils
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", "test_secret_key");
        // Даем время жизни токена в 1 час для теста
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationInMs", 3600000L);
    }

    @Test
    @DisplayName("createToken должен создавать валидный JWT с нужным username")
    void createTokenShouldReturnValidJwt() {
        String username = "testUser";
        String token = jwtTokenProvider.createToken(username);

        assertThat(token).isNotEmpty();

        // Проверяем, что можно спарсить токен без ошибок
        String subject = Jwts.parser()
                .setSigningKey("test_secret_key")
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        assertThat(subject).isEqualTo(username);
    }

    @Test
    @DisplayName("validateToken должен возвращать true для валидного токена")
    void validateTokenShouldReturnTrueForValidToken() {
        String token = jwtTokenProvider.createToken("testUser");

        boolean isValid = jwtTokenProvider.validateToken(token);
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("validateToken должен возвращать false для невалидного токена")
    void validateTokenShouldReturnFalseForInvalidToken() {
        // Создаем токен с другим ключом
        String invalidToken = Jwts.builder()
                .setSubject("testUser")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000L))
                .signWith(SignatureAlgorithm.HS512, "wrong_key")
                .compact();

        boolean isValid = jwtTokenProvider.validateToken(invalidToken);
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("getTokenFromAuthorizationHeader должен вернуть токен без 'Bearer '")
    void getTokenFromAuthorizationHeaderShouldReturnToken() {
        String header = "Bearer some_token_value";
        String token = jwtTokenProvider.getTokenFromAuthorizationHeader(header);
        assertThat(token).isEqualTo("some_token_value");
    }

    @Test
    @DisplayName("getUsernameFromJWT должен вернуть правильный username из токена")
    void getUsernameFromJWTShouldReturnCorrectUsername() {
        String username = "testUser";
        String token = jwtTokenProvider.createToken(username);

        String extractedUsername = jwtTokenProvider.getUsernameFromJWT(token);
        assertThat(extractedUsername).isEqualTo(username);
    }

    @Test
    @DisplayName("getUsernameFromAuthorizationHeader должен вернуть username из заголовка Authorization")
    void getUsernameFromAuthorizationHeaderShouldReturnUsername() {
        String username = "headerUser";
        String token = jwtTokenProvider.createToken(username);
        String header = "Bearer " + token;

        String extractedUsername = jwtTokenProvider.getUsernameFromAuthorizationHeader(header);
        assertThat(extractedUsername).isEqualTo(username);
    }
}

