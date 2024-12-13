package ru.mai.trpo.rest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.mai.trpo.configuration.jwt.JwtTokenProvider;
import ru.mai.trpo.dto.files.FileMetadataDto;
import ru.mai.trpo.dto.files.TextAnalysisResultDto;
import ru.mai.trpo.service.WordStatisticsService;

import lombok.RequiredArgsConstructor;

/**
 * REST-контроллер для получения информации о ранее проанализированных файлах и результатов их анализа.
 * <p>
 * Данный контроллер предоставляет следующие конечные точки:
 * <ul>
 *     <li>{@code GET /api/files/result} – получить список проанализированных файлов для аутентифицированного пользователя</li>
 *     <li>{@code GET /api/files/result/{id}} – получить результат анализа конкретного файла по его идентификатору</li>
 * </ul>
 * Для доступа к ресурсам контроллера требуется валидный JWT-токен, передаваемый в заголовке {@code Authorization}.
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FilesHistoryController {

    private final JwtTokenProvider jwtTokenProvider;
    private final WordStatisticsService wordStatisticsService;

    /**
     * Возвращает список метаданных о ранее проанализированных файлах для текущего пользователя.
     *
     * @param authorizationHeader заголовок Authorization с JWT-токеном
     * @return список {@link FileMetadataDto}, содержащий метаданные файлов
     */
    @GetMapping("/result")
    public ResponseEntity<List<FileMetadataDto>> getAnalyzedFilesForUser(@RequestHeader("Authorization") String authorizationHeader) {
        String username = jwtTokenProvider.getUsernameFromAuthorizationHeader(authorizationHeader);
        List<FileMetadataDto> metadata = wordStatisticsService.getAnalyzedFilesForUser(username);

        return ResponseEntity.ok(metadata);
    }

    /**
     * Возвращает результат анализа конкретного файла по его идентификатору для текущего пользователя.
     *
     * @param id идентификатор файла
     * @param authorizationHeader заголовок Authorization с JWT-токеном
     * @return объект {@link TextAnalysisResultDto}, содержащий результаты анализа
     */
    @GetMapping("/result/{id}")
    public ResponseEntity<TextAnalysisResultDto> getAnalysisResult(@PathVariable Long id, @RequestHeader("Authorization") String authorizationHeader) {
        String username = jwtTokenProvider.getUsernameFromAuthorizationHeader(authorizationHeader);
        TextAnalysisResultDto result = wordStatisticsService.getAnalysisResult(id, username);
        return ResponseEntity.ok(result);
    }
}
