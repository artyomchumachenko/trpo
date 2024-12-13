package ru.mai.trpo.configuration.jwt;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

/**
 * Провайдер функционала для работы с JWT-токенами.
 * <p>
 * Класс обеспечивает методы для:
 * <ul>
 *     <li>Создания JWT-токена на основе имени пользователя</li>
 *     <li>Валидации токена</li>
 *     <li>Извлечения имени пользователя (subject) из токена</li>
 *     <li>Извлечения токена из заголовка Authorization</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationInMs;

    /**
     * Создает JWT-токен для заданного имени пользователя.
     *
     * @param username имя пользователя
     * @return сформированный JWT-токен
     */
    public String createToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    /**
     * Валидирует переданный JWT-токен.
     *
     * @param token JWT-токен
     * @return true, если токен корректен, иначе false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Извлекает токен из заголовка Authorization, удаляя префикс "Bearer ".
     *
     * @param authorizationHeader значение заголовка Authorization
     * @return строка JWT-токена
     */
    public String getTokenFromAuthorizationHeader(String authorizationHeader) {
        return authorizationHeader.replace("Bearer ", "").trim();
    }

    /**
     * Извлекает имя пользователя из JWT-токена.
     *
     * @param token JWT-токен
     * @return имя пользователя (subject)
     */
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * Извлекает имя пользователя из заголовка Authorization (для этого извлекается токен, а затем – subject).
     *
     * @param authorizationHeader заголовок Authorization
     * @return имя пользователя
     */
    public String getUsernameFromAuthorizationHeader(String authorizationHeader) {
        String token = getTokenFromAuthorizationHeader(authorizationHeader);
        return getUsernameFromJWT(token);
    }
}
