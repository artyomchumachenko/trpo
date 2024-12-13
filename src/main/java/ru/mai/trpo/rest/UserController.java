package ru.mai.trpo.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.mai.trpo.configuration.jwt.JwtTokenProvider;
import ru.mai.trpo.dto.user.LoginRequest;
import ru.mai.trpo.dto.user.RegisterRequest;
import ru.mai.trpo.model.User;
import ru.mai.trpo.service.UserService;

import lombok.RequiredArgsConstructor;

/**
 * REST-контроллер для управления пользователями: регистрация и аутентификация.
 * <p>
 * Предоставляет следующие конечные точки:
 * <ul>
 *     <li>{@code POST /api/user/registration} – регистрация нового пользователя</li>
 *     <li>{@code POST /api/user/login} – аутентификация пользователя и получение JWT-токена</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    /**
     * Регистрирует нового пользователя в системе.
     *
     * @param request объект запроса, содержащий данные для регистрации (имя пользователя и пароль)
     * @return объект {@link ResponseEntity} с сообщением об успешной регистрации или ошибкой
     */
    @PostMapping("/registration")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest request) {
        if (userService.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Пользователь с таким именем уже существует.");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userService.saveUser(user);
        return ResponseEntity.ok("Пользователь успешно зарегистрирован.");
    }

    /**
     * Аутентифицирует пользователя по имени и паролю.
     *
     * @param request объект запроса, содержащий имя пользователя и пароль
     * @return объект {@link ResponseEntity} с JWT-токеном при успешной аутентификации или ошибкой 401 при неверных учетных данных
     */
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtTokenProvider.createToken(request.getUsername());

            return ResponseEntity.ok(jwt);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверные учетные данные");
        }
    }
}
