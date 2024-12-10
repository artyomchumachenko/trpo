package ru.mai.trpo.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import ru.mai.trpo.dto.files.FileMetadataDto;
import ru.mai.trpo.dto.files.TextAnalysisResultDto;
import ru.mai.trpo.dto.integration.SentenceResponseDto;
import ru.mai.trpo.dto.statistic.SyntacticRoleCountDto;
import ru.mai.trpo.dto.statistic.WordStatisticsDto;
import ru.mai.trpo.model.Sentence;
import ru.mai.trpo.model.Text;
import ru.mai.trpo.model.User;
import ru.mai.trpo.model.Word;
import ru.mai.trpo.repository.SentenceRepository;
import ru.mai.trpo.repository.TextRepository;
import ru.mai.trpo.repository.WordRepository;

import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WordStatisticsService {

    private final WordRepository wordRepository;
    private final TextRepository textRepository;
    private final SentenceRepository sentenceRepository;
    private final UserService userService;

    public List<WordStatisticsDto> getWordStatistics() {
        List<Tuple> rawStatistics = wordRepository.findRawWordStatistics();
        return mapToWordStatisticsDto(rawStatistics);
    }

    public List<WordStatisticsDto> getWordStatisticsByUsername(String username) {
        List<Tuple> rawStatistics = wordRepository.findWordStatisticsByUsername(username);
        return mapToWordStatisticsDto(rawStatistics);
    }

    public List<FileMetadataDto> getAnalyzedFilesForUser(String username) {
        return textRepository.findByUserUsername(username).stream()
                .map(text -> new FileMetadataDto(
                        text.getTextId(),
                        text.getFileName(),
                        text.getUploadDate()))
                .toList();
    }

    private List<WordStatisticsDto> mapToWordStatisticsDto(List<Tuple> rawStatistics) {
        return rawStatistics.stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get("wordText", String.class),
                        Collectors.mapping(
                                tuple -> new SyntacticRoleCountDto(
                                        tuple.get("roleDescription", String.class),
                                        tuple.get("count", Long.class)
                                ),
                                Collectors.toList()
                        )
                ))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey()) // Сортировка по алфавиту
                .map(entry -> new WordStatisticsDto(entry.getKey(), entry.getValue()))
                .toList();
    }

    public TextAnalysisResultDto getAnalysisResult(Long textId, String username) {
        // Получаем текст по ID
        Text text = textRepository.findById(textId)
                .orElseThrow(() -> new IllegalArgumentException("Файл с таким ID не найден"));
        User user = userService.getUserByUsername(username);
        if (!user.equals(text.getUser())) {
            throw new AccessDeniedException("У вас нет доступа к этому файлу");
        }

        // Получаем предложения, связанные с текстом
        List<Sentence> sentences = sentenceRepository.findByTextTextId(text.getTextId());

        // Преобразуем предложения в SentenceResponseDto
        List<SentenceResponseDto> analysisResults = sentences.stream()
                .map(sentence -> {
                    List<Word> words = wordRepository.findBySentenceSentenceId(sentence.getSentenceId());

                    return mapToSentenceResponseDto(sentence, words);
                })
                .toList();

        // Формируем результат
        TextAnalysisResultDto result = new TextAnalysisResultDto();
        result.setFileId(text.getTextId());
        result.setFileName(text.getFileName());
        result.setAnalysisResults(analysisResults);

        return result;
    }

    private SentenceResponseDto mapToSentenceResponseDto(Sentence sentence, List<Word> words) {
        SentenceResponseDto dto = new SentenceResponseDto();
        dto.setSentence(sentence.getContent());
        dto.setWords(words.stream().map(Word::getWordText).toList());
        dto.setLemmas(words.stream().map(Word::getLemma).toList());
        dto.setPosTags(words.stream()
                .map(word -> word.getPosTag() != null ? word.getPosTag().getCode() : null)
                .toList());
        dto.setDepTags(words.stream()
                .map(word -> word.getSyntacticRole() != null ? word.getSyntacticRole().getCode() : null)
                .toList());
        return dto;
    }

    public List<WordStatisticsDto> getWordStatisticsByFileId(Long fileId) {
        // Проверяем, существует ли текст с таким ID
        Text text = textRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("Файл с таким ID не найден"));

        // Собираем статистику по словам из предложений
        List<Tuple> rawStatistics = wordRepository.findWordStatisticsByTextId(fileId);

        // Преобразуем сырые данные в DTO
        return mapToWordStatisticsDto(rawStatistics);
    }

    public WordStatisticsDto getWordStatisticsByWord(String word) {
        // Получаем статистику для указанного слова из репозитория
        List<Tuple> rawStatistics = wordRepository.findWordStatisticsForWord(word);

        // Преобразуем данные в список SyntacticRoleCountDto
        List<SyntacticRoleCountDto> syntacticRoles = rawStatistics.stream()
                .map(tuple -> new SyntacticRoleCountDto(
                        tuple.get("roleDescription", String.class),
                        tuple.get("count", Long.class)
                ))
                .toList();

        // Создаем и возвращаем объект WordStatisticsDto
        return new WordStatisticsDto(word, syntacticRoles);
    }
}
