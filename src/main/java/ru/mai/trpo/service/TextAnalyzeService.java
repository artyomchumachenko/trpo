package ru.mai.trpo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ru.mai.trpo.dto.TextAnalyzeResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TextAnalyzeService {

    private final ObjectMapper objectMapper;

    public TextAnalyzeResponseDto analyzeText(MultipartFile file) {
        try {
            return objectMapper.readValue(getMockAnalyzeResponse(), TextAnalyzeResponseDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getMockAnalyzeResponse() {
        return """
            {
                "sentences": [
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
            }
                """;
    }
}
