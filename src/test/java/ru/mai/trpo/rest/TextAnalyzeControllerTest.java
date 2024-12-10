package ru.mai.trpo.rest;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import ru.mai.trpo.dto.integration.SentenceResponseDto;
import ru.mai.trpo.model.User;
import ru.mai.trpo.service.TextAnalyzeService;

import lombok.SneakyThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Юнит-тесты для TextAnalyzeController.
 *
 * Цели:
 * - Проверить, что контроллер корректно обрабатывает запрос на анализ текста.
 * - Убедиться, что результат анализа возвращается в ожидаемом формате.
 * - Убедиться, что заголовок Authorization корректно обрабатывается и используется для получения имени пользователя.
 */
@WebMvcTest(TextAnalyzeController.class)
public class TextAnalyzeControllerTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TextAnalyzeService textAnalyzeService;

    /**
     * Тест проверяет следующий сценарий:
     * 1) Пользователь отправляет POST-запрос на "/api/analyze" с файлом в формате .txt.
     * 2) В заголовке "Authorization" передаётся токен, из которого получается имя пользователя.
     * 3) Сервис textAnalyzeService возвращает заранее смоделированный массив SentenceResponseDto.
     * 4) Проверяется, что ответ содержит HTTP статус 200, ожидаемый JSON и корректные данные.
     */
    @Test
    @DisplayName("Должен вернуть результат анализа текста при корректных входных данных")
    @SneakyThrows
    void shouldReturnAnalyzeResult() {
        // Данные для теста
        String username = "test_user";
        User mockUser = new User();
        mockUser.setUsername(username);

        // Создаём фиктивный текстовый файл для отправки
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Это тестовый текст".getBytes()
        );

        // Модель ответа от сервиса анализа
        SentenceResponseDto responseDto = new SentenceResponseDto();
        responseDto.setSentence("Это тестовый текст");
        responseDto.setWords(List.of("Это", "тестовый", "текст"));
        responseDto.setLemmas(List.of("это", "тестовый", "текст"));
        responseDto.setPosTags(List.of("PRON", "ADJ", "NOUN"));
        responseDto.setDepTags(List.of("nsubj", "amod", "root"));
        responseDto.setHeadId(List.of(2, 3, 0));

        SentenceResponseDto[] serviceResult = new SentenceResponseDto[]{ responseDto };

        // Мокируем поведение зависимостей
        when(textAnalyzeService.analyzeText(any(), any())).thenReturn(serviceResult);

        // Выполняем запрос к контроллеру
        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/api/analyze")
                        .file(mockFile)
                        .header("Authorization", "Bearer valid_token")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sentence").value("Это тестовый текст"))
                .andExpect(jsonPath("$[0].words[0]").value("Это"))
                .andExpect(jsonPath("$[0].words[1]").value("тестовый"))
                .andExpect(jsonPath("$[0].words[2]").value("текст"))
                .andExpect(jsonPath("$[0].lemmas[0]").value("это"))
                .andExpect(jsonPath("$[0].lemmas[1]").value("тестовый"))
                .andExpect(jsonPath("$[0].lemmas[2]").value("текст"))
                .andExpect(jsonPath("$[0].pos_tags[0]").value("PRON"))
                .andExpect(jsonPath("$[0].pos_tags[1]").value("ADJ"))
                .andExpect(jsonPath("$[0].pos_tags[2]").value("NOUN"))
                .andExpect(jsonPath("$[0].dep_tags[0]").value("nsubj"))
                .andExpect(jsonPath("$[0].dep_tags[1]").value("amod"))
                .andExpect(jsonPath("$[0].dep_tags[2]").value("root"))
                .andExpect(jsonPath("$[0].head_id[0]").value(2))
                .andExpect(jsonPath("$[0].head_id[1]").value(3))
                .andExpect(jsonPath("$[0].head_id[2]").value(0));
    }
}
