package ru.mai.trpo.rest;

import java.time.LocalDateTime;

import javax.security.sasl.AuthenticationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import ru.mai.trpo.dto.ErrorResponseDto;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Глобальный обработчик исключений в приложении.
 * <p>
 * Данный класс перехватывает исключения, возникающие во время выполнения запросов к REST API,
 * и возвращает человеко-читаемые ответы в формате JSON или других сериализуемых форматов.
 * <p>
 * Обрабатываемые исключения:
 * <ul>
 *     <li>{@code Exception} – любые непредвиденные ошибки приложения</li>
 *     <li>{@code RuntimeException} – ошибки времени выполнения</li>
 *     <li>{@code NoHandlerFoundException} – ситуация, когда запрошенный эндпоинт не найден</li>
 *     <li>{@code AuthenticationException} – ошибки аутентификации, выбрасываемые Spring Security</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает все исключения, не подпадающие под более специфичные обработчики.
     *
     * @param ex исключение
     * @param request объект запроса, для извлечения деталей о контексте
     * @return объект {@link ResponseEntity} с информацией об ошибке и статусом 500 (Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleAllExceptions(Exception ex, WebRequest request) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Обрабатывает исключения типа {@code RuntimeException}.
     *
     * @param ex исключение времени выполнения
     * @param request текущий HTTP-запрос
     * @return объект {@link ResponseEntity} с информацией об ошибке и статусом 500 (Internal Server Error)
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDto> handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                "uri=" + request.getRequestURI(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Обрабатывает ситуацию, когда запрошенный эндпоинт не существует.
     *
     * @param ex исключение {@link NoHandlerFoundException}, указывающее, что обработчик для данного пути не найден
     * @return ответ с текстом о недоступности эндпоинта и статусом 400 (Bad Request)
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<String> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Эндпоинт не найден. Проверьте правильность запроса.");
    }

    /**
     * Обрабатывает исключения аутентификации, возникающие в Spring Security.
     * <p>
     * Если токен недействителен или отсутствует, выбрасывается {@link AuthenticationException},
     * в ответ на это возвращается статус 401 (Unauthorized) и информация об ошибке.
     *
     * @param ex исключение аутентификации
     * @param request текущий HTTP-запрос
     * @return объект {@link ResponseEntity} с информацией об ошибке и статусом 401 (Unauthorized)
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDto> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                401,
                ex.getMessage() != null ? ex.getMessage() : "Неверный токен",
                "uri=" + request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
}
