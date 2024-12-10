package ru.mai.trpo.rest;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import ru.mai.trpo.dto.files.FileMetadataDto;
import ru.mai.trpo.dto.files.TextAnalysisResultDto;
import ru.mai.trpo.dto.integration.SentenceResponseDto;
import ru.mai.trpo.service.WordStatisticsService;

import lombok.SneakyThrows;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilesHistoryController.class)
public class FilesHistoryControllerTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WordStatisticsService wordStatisticsService;

    @Test
    @DisplayName("Должен вернуть список проанализированных файлов для пользователя со статусом 200 OK")
    @SneakyThrows
    void shouldReturnAnalyzedFilesForUserWithOkStatus() {
        // Мокируем данные
        List<FileMetadataDto> mockMetadata = List.of(
                new FileMetadataDto(1L, "file1.txt", LocalDateTime.of(2024, 12, 1, 10, 30)),
                new FileMetadataDto(2L, "file2.docx", LocalDateTime.of(2024, 12, 2, 15, 45))
        );

        Mockito.when(jwtTokenProvider.getUsernameFromAuthorizationHeader("Bearer valid_token"))
                .thenReturn("test_user");
        Mockito.when(wordStatisticsService.getAnalyzedFilesForUser("test_user"))
                .thenReturn(mockMetadata);

        // Выполняем запрос и проверяем результат
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/files/result")
                        .header("Authorization", "Bearer valid_token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].fileName").value("file1.txt"))
                .andExpect(jsonPath("$[0].uploadDate").value("2024-12-01T10:30:00"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].fileName").value("file2.docx"))
                .andExpect(jsonPath("$[1].uploadDate").value("2024-12-02T15:45:00"));
    }

    @Test
    @DisplayName("Должен вернуть результат анализа текста по ID файла со статусом 200 OK")
    @SneakyThrows
    void shouldReturnAnalysisResultWithOkStatus() {
        // Мокируем данные
        TextAnalysisResultDto mockResult = new TextAnalysisResultDto();
        mockResult.setFileId(1L);
        mockResult.setFileName("file1.txt");
        mockResult.setAnalysisResults(List.of(
                new SentenceResponseDto(
                        "Sentence 1",
                        List.of("word1", "word2"),
                        List.of("lemma1", "lemma2"),
                        List.of("NOUN", "VERB"),
                        List.of("ROOT", "OBJ"),
                        List.of(0, 1)
                ),
                new SentenceResponseDto(
                        "Sentence 2",
                        List.of("word3"),
                        List.of("lemma3"),
                        List.of("ADJ"),
                        List.of("MOD"),
                        List.of(2)
                )
        ));

        Mockito.when(jwtTokenProvider.getUsernameFromAuthorizationHeader("Bearer valid_token"))
                .thenReturn("test_user");
        Mockito.when(wordStatisticsService.getAnalysisResult(1L, "test_user"))
                .thenReturn(mockResult);

        // Выполняем запрос и проверяем результат
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/files/result/1")
                        .header("Authorization", "Bearer valid_token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileId").value(1))
                .andExpect(jsonPath("$.fileName").value("file1.txt"))
                .andExpect(jsonPath("$.analysisResults[0].sentence").value("Sentence 1"))
                .andExpect(jsonPath("$.analysisResults[0].words[0]").value("word1"))
                .andExpect(jsonPath("$.analysisResults[0].words[1]").value("word2"))
                .andExpect(jsonPath("$.analysisResults[0].lemmas[0]").value("lemma1"))
                .andExpect(jsonPath("$.analysisResults[0].lemmas[1]").value("lemma2"))
                .andExpect(jsonPath("$.analysisResults[0].pos_tags[0]").value("NOUN"))
                .andExpect(jsonPath("$.analysisResults[0].pos_tags[1]").value("VERB"))
                .andExpect(jsonPath("$.analysisResults[0].dep_tags[0]").value("ROOT"))
                .andExpect(jsonPath("$.analysisResults[0].dep_tags[1]").value("OBJ"))
                .andExpect(jsonPath("$.analysisResults[0].head_id[0]").value(0))
                .andExpect(jsonPath("$.analysisResults[0].head_id[1]").value(1))
                .andExpect(jsonPath("$.analysisResults[1].sentence").value("Sentence 2"))
                .andExpect(jsonPath("$.analysisResults[1].words[0]").value("word3"))
                .andExpect(jsonPath("$.analysisResults[1].lemmas[0]").value("lemma3"))
                .andExpect(jsonPath("$.analysisResults[1].pos_tags[0]").value("ADJ"))
                .andExpect(jsonPath("$.analysisResults[1].dep_tags[0]").value("MOD"))
                .andExpect(jsonPath("$.analysisResults[1].head_id[0]").value(2));
    }
}
