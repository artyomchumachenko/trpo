package ru.mai.trpo.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ru.mai.trpo.configuration.jwt.JwtTokenProvider;
import ru.mai.trpo.dto.integration.SentenceResponseDto;
import ru.mai.trpo.model.User;
import ru.mai.trpo.service.TextAnalyzeService;
import ru.mai.trpo.service.UserService;

import lombok.RequiredArgsConstructor;

/**
 * Основной контроллер для обработки запросов на анализ текста
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TextAnalyzeController {

    private final TextAnalyzeService textAnalyzeService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    /**
     * Запрос на синхронный анализ текста через Python модель
     * @param file Файл с текстом (.txt / .pdf)
     * @return Результат анализа
     */
    @PostMapping("/analyze")
    public ResponseEntity<SentenceResponseDto[]> analyzeText(@RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String authorizationHeader) {
        String username = jwtTokenProvider.getUsernameFromAuthorizationHeader(authorizationHeader);
        User user = userService.getUserByUsername(username);
        SentenceResponseDto[] result = textAnalyzeService.analyzeText(file, user);
        return ResponseEntity.ok(result);
    }
}
