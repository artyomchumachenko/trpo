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

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FilesHistoryController {

    private final JwtTokenProvider jwtTokenProvider;
    private final WordStatisticsService wordStatisticsService;

    @GetMapping("/result")
    public ResponseEntity<List<FileMetadataDto>> getAnalyzedFilesForUser(@RequestHeader("Authorization") String authorizationHeader) {
        // Получаем username из AuthHeader
        String username = jwtTokenProvider.getUsernameFromAuthorizationHeader(authorizationHeader);

        // Получаем список метаданных файлов
        List<FileMetadataDto> metadata = wordStatisticsService.getAnalyzedFilesForUser(username);

        return ResponseEntity.ok(metadata);
    }

    @GetMapping("/result/{id}")
    public ResponseEntity<TextAnalysisResultDto> getAnalysisResult(@PathVariable Long id, @RequestHeader("Authorization") String authorizationHeader) {
        // Получаем username из AuthHeader
        String username = jwtTokenProvider.getUsernameFromAuthorizationHeader(authorizationHeader);
        TextAnalysisResultDto result = wordStatisticsService.getAnalysisResult(id, username);
        return ResponseEntity.ok(result);
    }
}
