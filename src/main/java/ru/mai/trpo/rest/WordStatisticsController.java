package ru.mai.trpo.rest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.mai.trpo.configuration.jwt.JwtTokenProvider;
import ru.mai.trpo.dto.statistic.WordStatisticsDto;
import ru.mai.trpo.service.WordStatisticsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class WordStatisticsController {

    private final WordStatisticsService wordStatisticsService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/words")
    public ResponseEntity<List<WordStatisticsDto>> getWordStatistics() {
        List<WordStatisticsDto> statistics = wordStatisticsService.getWordStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/words/me")
    public ResponseEntity<List<WordStatisticsDto>> getWordStatisticsForUser(
            @RequestHeader("Authorization") String authorizationHeader) {
        // Извлекаем username из authorizationHeader
        String username = jwtTokenProvider.getUsernameFromAuthorizationHeader(authorizationHeader);

        // Получаем статистику по username
        List<WordStatisticsDto> statistics = wordStatisticsService.getWordStatisticsByUsername(username);

        return ResponseEntity.ok(statistics);
    }

    /** Получение статистики по ID файла */
    @GetMapping("/words/file/{fileId}")
    public ResponseEntity<List<WordStatisticsDto>> getWordStatisticsByFileId(@PathVariable Long fileId) {
        List<WordStatisticsDto> statistics = wordStatisticsService.getWordStatisticsByFileId(fileId);
        return ResponseEntity.ok(statistics);
    }
}
