package ru.mai.trpo.dto.integration;

import lombok.Builder;
import lombok.Data;

/**
 * Класс данных для запроса на анализ текста
 */
@Data
@Builder
public class TextRequestDto {
    private String text;
}
