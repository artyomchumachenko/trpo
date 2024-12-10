package ru.mai.trpo.rest;

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

import ru.mai.trpo.dto.statistic.SyntacticRoleCountDto;
import ru.mai.trpo.dto.statistic.WordStatisticsDto;
import ru.mai.trpo.service.WordStatisticsService;

import lombok.SneakyThrows;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WordStatisticsController.class)
public class WordStatisticsControllerTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WordStatisticsService wordStatisticsService;

    @Test
    @DisplayName("Должен вернуть список статистики слов с синтаксическими ролями со статусом 200 OK")
    @SneakyThrows
    void shouldReturnWordStatisticsWithSyntacticRolesAndOkStatus() {
        // Мокируем результат сервиса
        List<WordStatisticsDto> mockStatistics = List.of(
                new WordStatisticsDto(
                        "слово1",
                        List.of(
                                new SyntacticRoleCountDto("подлежащее", 10L),
                                new SyntacticRoleCountDto("дополнение", 5L)
                        )
                ),
                new WordStatisticsDto(
                        "слово2",
                        List.of(
                                new SyntacticRoleCountDto("сказуемое", 8L)
                        )
                )
        );

        Mockito.when(wordStatisticsService.getWordStatistics()).thenReturn(mockStatistics);

        // Выполняем запрос и проверяем результат
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/statistics/words")
                        .header("Authorization", "Bearer valid_token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Проверяем статус 200 OK
                .andExpect(jsonPath("$[0].wordText").value("слово1")) // Проверяем содержимое
                .andExpect(jsonPath("$[0].roles[0].syntacticRoleDescription").value("подлежащее"))
                .andExpect(jsonPath("$[0].roles[0].count").value(10))
                .andExpect(jsonPath("$[0].roles[1].syntacticRoleDescription").value("дополнение"))
                .andExpect(jsonPath("$[0].roles[1].count").value(5))
                .andExpect(jsonPath("$[1].wordText").value("слово2"))
                .andExpect(jsonPath("$[1].roles[0].syntacticRoleDescription").value("сказуемое"))
                .andExpect(jsonPath("$[1].roles[0].count").value(8));
    }

    @Test
    @DisplayName("Должен вернуть статистику слов по ID файла со статусом 200 OK")
    @SneakyThrows
    void shouldReturnWordStatisticsByFileIdWithOkStatus() {
        // Мокируем результат сервиса
        List<WordStatisticsDto> mockStatistics = List.of(
                new WordStatisticsDto("слово1", List.of(new SyntacticRoleCountDto("подлежащее", 7L)))
        );

        Mockito.when(wordStatisticsService.getWordStatisticsByFileId(123L)).thenReturn(mockStatistics);

        // Выполняем запрос и проверяем результат
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/statistics/words/file/123")
                        .header("Authorization", "Bearer valid_token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].wordText").value("слово1"))
                .andExpect(jsonPath("$[0].roles[0].syntacticRoleDescription").value("подлежащее"))
                .andExpect(jsonPath("$[0].roles[0].count").value(7));
    }

    @Test
    @DisplayName("Должен вернуть статистику по конкретному слову со статусом 200 OK")
    @SneakyThrows
    void shouldReturnWordStatisticsByWordWithOkStatus() {
        // Мокируем результат сервиса
        WordStatisticsDto mockStatistic = new WordStatisticsDto(
                "слово1",
                List.of(new SyntacticRoleCountDto("подлежащее", 3L))
        );

        Mockito.when(wordStatisticsService.getWordStatisticsByWord("слово1")).thenReturn(mockStatistic);

        // Выполняем запрос и проверяем результат
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/statistics/words/search")
                        .param("word", "слово1")
                        .header("Authorization", "Bearer valid_token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wordText").value("слово1"))
                .andExpect(jsonPath("$.roles[0].syntacticRoleDescription").value("подлежащее"))
                .andExpect(jsonPath("$.roles[0].count").value(3));
    }
}
