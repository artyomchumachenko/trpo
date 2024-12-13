package ru.mai.trpo.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ru.mai.trpo.client.TextAnalyzePyModelClient;
import ru.mai.trpo.dto.integration.SentenceResponseDto;
import ru.mai.trpo.dto.integration.TextRequestDto;
import ru.mai.trpo.model.PosTag;
import ru.mai.trpo.model.Sentence;
import ru.mai.trpo.model.SyntacticRole;
import ru.mai.trpo.model.Text;
import ru.mai.trpo.model.User;
import ru.mai.trpo.model.Word;
import ru.mai.trpo.repository.PosTagRepository;
import ru.mai.trpo.repository.SentenceRepository;
import ru.mai.trpo.repository.SyntacticRoleRepository;
import ru.mai.trpo.repository.TextRepository;
import ru.mai.trpo.repository.WordRepository;
import ru.mai.trpo.rest.MockController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Сервис бизнес-логики, связанный с анализом текста.
 * <p>
 * Данный сервис:
 * <ul>
 *     <li>Извлекает текст из файла</li>
 *     <li>Отправляет текст на анализ в Python-модель</li>
 *     <li>Сохраняет результаты анализа (предложения, слова, теги) в базу данных</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TextAnalyzeService {

    private final TextAnalyzePyModelClient client;
    private final FileTextExtractor extractor;

    private final TextRepository textRepository;
    private final SentenceRepository sentenceRepository;
    private final WordRepository wordRepository;
    private final PosTagRepository posTagRepository;
    private final SyntacticRoleRepository syntacticRoleRepository;

    /**
     * Анализирует текстовый файл, извлекает текст, отправляет его на анализ в PyModel,
     * затем сохраняет результаты анализа в базе данных.
     *
     * @param file файл с текстовым содержимым для анализа
     * @param user пользователь, загрузивший файл
     * @return массив объектов {@link SentenceResponseDto}, содержащий результаты анализа по предложениям
     * @throws RuntimeException при ошибках чтения файла или сохранения данных
     */
    public SentenceResponseDto[] analyzeText(MultipartFile file, User user) {
        log.info("Start analyze processing for file: {} in Service", file.getOriginalFilename());
        String textContent = extractor.extractTextFromFile(file);
        log.info("Text content extract success for file: {}", file.getOriginalFilename());

        TextRequestDto requestDto = TextRequestDto.builder()
                .text(textContent)
                .build();

        // Отправка запроса в PyModel
        SentenceResponseDto[] response = client.analyzeText(requestDto);
        log.info("Response from AI model success received for file: {}, response: {}", file.getOriginalFilename(), response);

        // Сохраняем информацию о тексте
        Text text = new Text();
        text.setFileName(file.getOriginalFilename());
        try {
            text.setContent(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        text.setUploadDate(LocalDateTime.now());
        text.setUser(user);
        text = textRepository.save(text);

        // Предзагрузка POS-тегов и синтаксических ролей
        Map<String, PosTag> posTagMap = posTagRepository.findAll()
                .stream()
                .collect(Collectors.toMap(PosTag::getCode, Function.identity()));
        Map<String, SyntacticRole> syntacticRoleMap = syntacticRoleRepository.findAll()
                .stream()
                .collect(Collectors.toMap(SyntacticRole::getCode, Function.identity()));

        int sentenceNumber = 1;
        List<Sentence> sentences = new ArrayList<>();
        List<Word> words = new ArrayList<>();

        // Обработка результата анализа
        for (SentenceResponseDto sentenceDto : response) {
            Sentence sentence = new Sentence();
            sentence.setText(text);
            sentence.setContent(sentenceDto.getSentence());
            sentence.setSentenceNumber(sentenceNumber++);
            sentences.add(sentence);

            List<String> wordTexts = sentenceDto.getWords();
            List<String> lemmas = sentenceDto.getLemmas();
            List<String> posTags = sentenceDto.getPosTags();
            List<String> depTags = sentenceDto.getDepTags();

            for (int i = 0; i < wordTexts.size(); i++) {
                Word word = new Word();
                word.setSentence(sentence);
                word.setWordText(wordTexts.get(i));
                word.setLemma(lemmas.get(i));

                // Установка POS-тега
                String posTagCode = posTags.get(i);
                PosTag posTag = posTagMap.get(posTagCode);
                if (posTag == null) {
                    posTag = new PosTag();
                    posTag.setCode(posTagCode);
                    posTag.setDescription(posTagCode);
                    posTag = posTagRepository.save(posTag);
                    posTagMap.put(posTagCode, posTag);
                }
                word.setPosTag(posTag);

                // Установка синтаксической роли
                String depTagCode = depTags.get(i);
                SyntacticRole syntacticRole = syntacticRoleMap.get(depTagCode);
                if (syntacticRole == null) {
                    syntacticRole = new SyntacticRole();
                    syntacticRole.setCode(depTagCode);
                    syntacticRole.setDescription(depTagCode);
                    syntacticRole = syntacticRoleRepository.save(syntacticRole);
                    syntacticRoleMap.put(depTagCode, syntacticRole);
                }
                word.setSyntacticRole(syntacticRole);

                words.add(word);
            }
        }

        // Сохранение предложений и слов
        sentenceRepository.saveAll(sentences);
        wordRepository.saveAll(words);

        log.info("All service logic finished, then return response to FRONT");
        return response;
    }
}
