package ru.mai.trpo.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ru.mai.trpo.dto.integration.SentenceResponseDto;

import lombok.RequiredArgsConstructor;

/**
 * Класс контроллер с ответами "заглушками" для имитации работы эндпоинтов приложения
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/mock")
public class MockController {

    private final ObjectMapper objectMapper;

    @PostMapping("/analyze")
    public ResponseEntity<SentenceResponseDto[]> analyzeText(@RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(
                    objectMapper.readValue(getMockAnalyzeResponse(), SentenceResponseDto[].class)
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getMockAnalyzeResponse() {
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
