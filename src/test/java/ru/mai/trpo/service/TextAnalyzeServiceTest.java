package ru.mai.trpo.service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import ru.mai.trpo.client.TextAnalyzePyModelClient;
import ru.mai.trpo.dto.integration.SentenceResponseDto;
import ru.mai.trpo.dto.integration.TextRequestDto;
import ru.mai.trpo.model.PosTag;
import ru.mai.trpo.model.Sentence;
import ru.mai.trpo.model.SyntacticRole;
import ru.mai.trpo.model.Text;
import ru.mai.trpo.model.Word;
import ru.mai.trpo.repository.PosTagRepository;
import ru.mai.trpo.repository.SentenceRepository;
import ru.mai.trpo.repository.SyntacticRoleRepository;
import ru.mai.trpo.repository.TextRepository;
import ru.mai.trpo.repository.WordRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TextAnalyzeServiceTest {

    @InjectMocks
    private TextAnalyzeService textAnalyzeService;

    @Mock
    private FileTextExtractor fileTextExtractor;

    @Mock
    private TextAnalyzePyModelClient client;

    @Mock
    private TextRepository textRepository;

    @Mock
    private SentenceRepository sentenceRepository;

    @Mock
    private WordRepository wordRepository;

    @Mock
    private PosTagRepository posTagRepository;

    @Mock
    private SyntacticRoleRepository syntacticRoleRepository;

    @Captor
    private ArgumentCaptor<Text> textCaptor;

    @Captor
    private ArgumentCaptor<List<Sentence>> sentencesCaptor;

    @Captor
    private ArgumentCaptor<List<Word>> wordsCaptor;

    @Captor
    private ArgumentCaptor<TextRequestDto> requestDtoCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void analyzeText_ShouldProcessAndSaveAnalysisResults() {
        // Текстовое содержимое файла
        String fileContent = "Тонкий луч утреннего солнца пробился сквозь занавеску и " +
                "осветил старую книгу, лежащую на пыльной полке.";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                fileContent.getBytes(StandardCharsets.UTF_8)
        );

        // Мокаем client.analyzeText(requestDto)
        SentenceResponseDto[] mockResponse = getMockAnalyzeResponse();
        when(client.analyzeText(any(TextRequestDto.class))).thenReturn(mockResponse);
        when(fileTextExtractor.extractTextFromFile(file)).thenReturn(fileContent);

        // Мокаем сохранение текста
        when(textRepository.save(any(Text.class))).thenAnswer(invocation -> {
            Text text = invocation.getArgument(0);
            text.setTextId(1L); // Устанавливаем ID после сохранения
            return text;
        });

        // Мокаем получение POS-тегов и синтаксических ролей
        when(posTagRepository.findAll()).thenReturn(Collections.emptyList());
        when(syntacticRoleRepository.findAll()).thenReturn(Collections.emptyList());

        // Мокаем сохранение новых POS-тегов и синтаксических ролей
        when(posTagRepository.save(any(PosTag.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(syntacticRoleRepository.save(any(SyntacticRole.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Мокаем сохранение предложений и слов
        when(sentenceRepository.saveAll(anyList())).thenReturn(null);
        when(wordRepository.saveAll(anyList())).thenReturn(null);

        // Вызов тестируемого метода
        SentenceResponseDto[] result = textAnalyzeService.analyzeText(file);

        // Проверка результата
        assertNotNull(result);
        assertEquals(mockResponse.length, result.length);

        // Проверяем, что текст был сохранен
        verify(textRepository, times(1)).save(textCaptor.capture());
        Text savedText = textCaptor.getValue();
        assertEquals("test.txt", savedText.getFileName());
        assertArrayEquals(fileContent.getBytes(StandardCharsets.UTF_8), savedText.getContent());

        // Проверяем, что client.analyzeText был вызван с правильным аргументом
        verify(client, times(1)).analyzeText(requestDtoCaptor.capture());
        TextRequestDto capturedRequestDto = requestDtoCaptor.getValue();
        assertEquals(fileContent, capturedRequestDto.getText());

        // Проверяем, что предложения были сохранены
        verify(sentenceRepository, times(1)).saveAll(sentencesCaptor.capture());
        List<Sentence> savedSentences = sentencesCaptor.getValue();
        assertEquals(mockResponse.length, savedSentences.size());

        // Проверяем, что слова были сохранены
        verify(wordRepository, times(1)).saveAll(wordsCaptor.capture());
        List<Word> savedWords = wordsCaptor.getValue();
        int totalWords = Arrays.stream(mockResponse).mapToInt(s -> s.getWords().size()).sum();
        assertEquals(totalWords, savedWords.size());

        // Дополнительные проверки
        Word firstWord = savedWords.get(0);
        assertEquals("Тонкий", firstWord.getWordText());
        assertEquals("тонкий", firstWord.getLemma());
        assertEquals("ADJ", firstWord.getPosTag().getCode());
        assertEquals("Определение", firstWord.getSyntacticRole().getCode());
    }

    private SentenceResponseDto[] getMockAnalyzeResponse() {
        SentenceResponseDto sentenceDto = new SentenceResponseDto();
        sentenceDto.setSentence("Тонкий луч утреннего солнца пробился сквозь занавеску и осветил старую книгу, лежащую на пыльной полке.");
        sentenceDto.setWords(Arrays.asList("Тонкий", "луч", "утреннего", "солнца", "пробился", "сквозь", "занавеску", "и", "осветил", "старую", "книгу", ",", "лежащую", "на", "пыльной", "полке", "."));
        sentenceDto.setLemmas(Arrays.asList("тонкий", "луч", "утренний", "солнце", "пробиться", "сквозь", "занавеска", "и", "осветить", "старый", "книга", ",", "лежать", "на", "пыльный", "полка", "."));
        sentenceDto.setPosTags(Arrays.asList("ADJ", "NOUN", "ADJ", "NOUN", "VERB", "ADP", "NOUN", "CCONJ", "VERB", "ADJ", "NOUN", "PUNCT", "VERB", "ADP", "ADJ", "NOUN", "PUNCT"));
        sentenceDto.setDepTags(Arrays.asList("Определение", "Подлежащее", "Определение", "Дополнение", "Сказуемое", "Предлог", "Обстоятельство", "Союз", "Однородный член", "Определение", "Дополнение", "Пунктуация", "Причастный оборот", "Предлог", "Определение", "Обстоятельство", "Пунктуация"));
        sentenceDto.setHeadId(Arrays.asList(2, 5, 4, 2, 0, 7, 5, 9, 5, 11, 9, 13, 11, 16, 16, 13, 5));

        return new SentenceResponseDto[]{sentenceDto};
    }
}
