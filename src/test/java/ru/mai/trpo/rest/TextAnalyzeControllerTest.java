package ru.mai.trpo.rest;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import ru.mai.trpo.dto.SentenceResponseDto;
import ru.mai.trpo.service.TextAnalyzeService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TextAnalyzeController.class)
public class TextAnalyzeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TextAnalyzeService textAnalyzeService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testAnalyzeText() throws Exception {
        // Подготовка данных
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Тонкий луч утреннего солнца пробился сквозь занавеску и осветил старую книгу, лежащую на пыльной полке.".getBytes()
        );

        // Получение ожидаемого JSON-ответа от модели после анализа текста
        String mockJsonResponse = getMockAnalyzeResponse();

        // Преобразование JSON в объект TextAnalyzeResponseDto
        SentenceResponseDto[] responseDto = objectMapper.readValue(mockJsonResponse, SentenceResponseDto[].class);

        // Мок ответа сервиса
        when(textAnalyzeService.analyzeText(file))
                .thenReturn(responseDto);

        // Выполнение теста
        mockMvc.perform(multipart("/api/analyze")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].sentence").value("Тонкий луч утреннего солнца пробился сквозь занавеску и осветил старую книгу, лежащую на пыльной полке."))
                .andExpect(jsonPath("$.[0].words[0]").value("Тонкий"))
                .andExpect(jsonPath("$.[0].lemmas[0]").value("тонкий"))
                .andExpect(jsonPath("$.[0].pos_tags[0]").value("ADJ"))
                .andExpect(jsonPath("$.[0].dep_tags[0]").value("Определение"))
                .andExpect(jsonPath("$.[0].head_id[0]").value(2));
    }

    private String getMockAnalyzeResponse() {
        return """
                [
                    {
                        "sentence": "Тонкий луч утреннего солнца пробился сквозь занавеску и осветил старую книгу, лежащую на пыльной полке.",
                        "words": [
                            "Тонкий",
                            "луч",
                            "утреннего",
                            "солнца",
                            "пробился",
                            "сквозь",
                            "занавеску",
                            "и",
                            "осветил",
                            "старую",
                            "книгу",
                            ",",
                            "лежащую",
                            "на",
                            "пыльной",
                            "полке",
                            "."
                        ],
                        "lemmas": [
                            "тонкий",
                            "луч",
                            "утренний",
                            "солнце",
                            "пробиться",
                            "сквозь",
                            "занавеска",
                            "и",
                            "осветить",
                            "старый",
                            "книга",
                            ",",
                            "лежать",
                            "на",
                            "пыльный",
                            "полка",
                            "."
                        ],
                        "pos_tags": [
                            "ADJ",
                            "NOUN",
                            "ADJ",
                            "NOUN",
                            "VERB",
                            "ADP",
                            "NOUN",
                            "CCONJ",
                            "VERB",
                            "ADJ",
                            "NOUN",
                            "PUNCT",
                            "VERB",
                            "ADP",
                            "ADJ",
                            "NOUN",
                            "PUNCT"
                        ],
                        "dep_tags": [
                            "Определение",
                            "Подлежащее",
                            "Определение",
                            "Дополнение",
                            "Сказуемое",
                            "Предлог",
                            "Обстоятельство",
                            "Союз",
                            "Однородный член",
                            "Определение",
                            "Дополнение",
                            "Пунктуация",
                            "Причастный оборот",
                            "Предлог",
                            "Определение",
                            "Обстоятельство",
                            "Пунктуация"
                        ],
                        "head_id": [
                            2,
                            5,
                            4,
                            2,
                            0,
                            7,
                            5,
                            9,
                            5,
                            11,
                            9,
                            13,
                            11,
                            16,
                            16,
                            13,
                            5
                        ]
                    }
                ]
                """;
    }
}