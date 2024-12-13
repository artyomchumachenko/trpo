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

/**
 * Сервис для работы со статистикой по словам.
 * <p>
 * Предоставляет методы для получения различной статистики по словам, анализируемым в системе:
 * <ul>
 *     <li>Общей статистики по всем словам</li>
 *     <li>Статистики по конкретному пользователю</li>
 *     <li>Списка проанализированных файлов для конкретного пользователя</li>
 *     <li>Статистики по конкретному файлу</li>
 *     <li>Статистики по конкретному слову</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class WordStatisticsService {

    private final WordRepository wordRepository;
    private final TextRepository textRepository;
    private final SentenceRepository sentenceRepository;
    private final UserService userService;

    /**
     * Получает общую статистику по всем словам, встречающимся в тексте, включая количество их употреблений
     * и связанные с ними синтаксические роли.
     *
     * @return список объектов {@link WordStatisticsDto} с детализацией по каждому слову
     */
    public List<WordStatisticsDto> getWordStatistics() {
        List<Tuple> rawStatistics = wordRepository.findRawWordStatistics();
        return mapToWordStatisticsDto(rawStatistics);
    }

    /**
     * Получает статистику слов для определённого пользователя.
     *
     * @param username имя пользователя
     * @return список {@link WordStatisticsDto} со статистикой слов, принадлежащих текстам данного пользователя
     */
    public List<WordStatisticsDto> getWordStatisticsByUsername(String username) {
        List<Tuple> rawStatistics = wordRepository.findWordStatisticsByUsername(username);
        return mapToWordStatisticsDto(rawStatistics);
    }

    /**
     * Получает список метаданных о ранее проанализированных файлах для пользователя.
     *
     * @param username имя пользователя
     * @return список {@link FileMetadataDto}, содержащий ID текста, имя файла и дату загрузки
     */
    public List<FileMetadataDto> getAnalyzedFilesForUser(String username) {
        return textRepository.findByUserUsername(username).stream()
                .map(text -> new FileMetadataDto(
                        text.getTextId(),
                        text.getFileName(),
                        text.getUploadDate()))
                .toList();
    }

    /**
     * Получает результаты анализа конкретного текста по его ID для указанного пользователя.
     *
     * @param textId ID текста
     * @param username имя пользователя
     * @return объект {@link TextAnalysisResultDto}, содержащий детализированные результаты анализа текста
     * @throws IllegalArgumentException если текст с таким ID не найден
     * @throws AccessDeniedException если пользователь не имеет доступа к данному тексту
     */
    public TextAnalysisResultDto getAnalysisResult(Long textId, String username) {
        Text text = textRepository.findById(textId)
                .orElseThrow(() -> new IllegalArgumentException("Файл с таким ID не найден"));

        User user = userService.getUserByUsername(username);
        if (!user.equals(text.getUser())) {
            throw new AccessDeniedException("У вас нет доступа к этому файлу");
        }

        List<Sentence> sentences = sentenceRepository.findByTextTextId(text.getTextId());

        List<SentenceResponseDto> analysisResults = sentences.stream()
                .map(sentence -> {
                    List<Word> words = wordRepository.findBySentenceSentenceId(sentence.getSentenceId());
                    return mapToSentenceResponseDto(sentence, words);
                })
                .toList();

        TextAnalysisResultDto result = new TextAnalysisResultDto();
        result.setFileId(text.getTextId());
        result.setFileName(text.getFileName());
        result.setAnalysisResults(analysisResults);

        return result;
    }

    /**
     * Получает статистику слов для конкретного файла по его ID.
     *
     * @param fileId ID файла (текста)
     * @return список {@link WordStatisticsDto} для указанного файла
     * @throws IllegalArgumentException если текст с таким ID не найден
     */
    public List<WordStatisticsDto> getWordStatisticsByFileId(Long fileId) {
        textRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("Файл с таким ID не найден"));

        List<Tuple> rawStatistics = wordRepository.findWordStatisticsByTextId(fileId);
        return mapToWordStatisticsDto(rawStatistics);
    }

    /**
     * Получает статистику для конкретного слова.
     *
     * @param word слово, для которого требуется получить статистику
     * @return объект {@link WordStatisticsDto}, содержащий синтаксические роли и их количество для данного слова
     */
    public WordStatisticsDto getWordStatisticsByWord(String word) {
        List<Tuple> rawStatistics = wordRepository.findWordStatisticsForWord(word);

        List<SyntacticRoleCountDto> syntacticRoles = rawStatistics.stream()
                .map(tuple -> new SyntacticRoleCountDto(
                        tuple.get("roleDescription", String.class),
                        tuple.get("count", Long.class)
                ))
                .toList();

        return new WordStatisticsDto(word, syntacticRoles);
    }

    /**
     * Преобразует сырые данные из запросов к базе данных в список DTO объектов {@link WordStatisticsDto}.
     *
     * @param rawStatistics список кортежей (Tuple), полученных из репозитория
     * @return список объектов {@link WordStatisticsDto}
     */
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
                .sorted(Map.Entry.comparingByKey()) // сортировка по названию слова
                .map(entry -> new WordStatisticsDto(entry.getKey(), entry.getValue()))
                .toList();
    }

    /**
     * Формирует DTO-объект для одного предложения и связанного с ним списка слов.
     *
     * @param sentence сущность предложения
     * @param words список сущностей слов, связанных с данным предложением
     * @return объект {@link SentenceResponseDto}, содержащий предложения, слова, леммы, POS-теги и синтаксические роли
     */
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
}
