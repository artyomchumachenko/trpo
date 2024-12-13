package ru.mai.trpo.rest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.mai.trpo.configuration.jwt.JwtTokenProvider;
import ru.mai.trpo.dto.statistic.WordStatisticsDto;
import ru.mai.trpo.service.WordStatisticsService;

import lombok.RequiredArgsConstructor;

/**
 * REST-контроллер для получения статистики по словам.
 * <p>
 * Предоставляет различные эндпоинты для получения статистики слов из анализируемых файлов:
 * <ul>
 *     <li>{@code GET /api/statistics/words} – получить общую статистику по всем проанализированным словам</li>
 *     <li>{@code GET /api/statistics/words/me} – получить статистику слов, относящуюся к текущему аутентифицированному пользователю</li>
 *     <li>{@code GET /api/statistics/words/file/{fileId}} – получить статистику слов для конкретного файла по его ID</li>
 *     <li>{@code GET /api/statistics/words/search?word=...} – получить статистику для конкретного слова</li>
 * </ul>
 * Все методы, требующие аутентификации, предполагают наличие валидного JWT в заголовке Authorization.
 */
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class WordStatisticsController {

    private final WordStatisticsService wordStatisticsService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Получает общую статистику по всем проанализированным словам.
     *
     * @return список объектов {@link WordStatisticsDto}, содержащих статистику
     */
    @GetMapping("/words")
    public ResponseEntity<List<WordStatisticsDto>> getWordStatistics() {
        List<WordStatisticsDto> statistics = wordStatisticsService.getWordStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * Получает статистику слов для текущего аутентифицированного пользователя.
     *
     * @param authorizationHeader заголовок Authorization с JWT-токеном
     * @return список {@link WordStatisticsDto}, отфильтрованный по пользователю
     */
    @GetMapping("/words/me")
    public ResponseEntity<List<WordStatisticsDto>> getWordStatisticsForUser(
            @RequestHeader("Authorization") String authorizationHeader) {
        String username = jwtTokenProvider.getUsernameFromAuthorizationHeader(authorizationHeader);
        List<WordStatisticsDto> statistics = wordStatisticsService.getWordStatisticsByUsername(username);
        return ResponseEntity.ok(statistics);
    }

    /**
     * Получает статистику слов по ID файла.
     *
     * @param fileId идентификатор файла
     * @return список {@link WordStatisticsDto} для указанного файла
     */
    @GetMapping("/words/file/{fileId}")
    public ResponseEntity<List<WordStatisticsDto>> getWordStatisticsByFileId(@PathVariable Long fileId) {
        List<WordStatisticsDto> statistics = wordStatisticsService.getWordStatisticsByFileId(fileId);
        return ResponseEntity.ok(statistics);
    }

    /**
     * Получает статистику для конкретного слова.
     *
     * @param word слово, для которого требуется получить статистику
     * @return объект {@link WordStatisticsDto}, содержащий данные по указанному слову
     */
    @GetMapping("/words/search")
    public ResponseEntity<WordStatisticsDto> getWordStatisticsByWord(@RequestParam String word) {
        WordStatisticsDto statistics = wordStatisticsService.getWordStatisticsByWord(word);
        return ResponseEntity.ok(statistics);
    }
}
