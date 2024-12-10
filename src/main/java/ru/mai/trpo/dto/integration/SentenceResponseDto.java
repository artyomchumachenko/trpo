package ru.mai.trpo.dto.integration;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс данных ответа от анализатора текста на Python
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SentenceResponseDto {
    private String sentence;
    private List<String> words;
    private List<String> lemmas;
    @JsonProperty("pos_tags")
    private List<String> posTags;
    @JsonProperty("dep_tags")
    private List<String> depTags;
    @JsonProperty("head_id")
    private List<Integer> headId;
}
