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
 * Сервис "бизнес-логики", связанной с анализом текста
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TextAnalyzeService {

    private final TextAnalyzePyModelClient client;
    private final FileTextExtractor extractor;

    // Инжектируем репозитории
    private final TextRepository textRepository;
    private final SentenceRepository sentenceRepository;
    private final WordRepository wordRepository;
    private final PosTagRepository posTagRepository;
    private final SyntacticRoleRepository syntacticRoleRepository;

    /**
     * Метод синхронного анализа текста
     * @param file Файл с текстом
     * @return Результат анализа
     */
    public SentenceResponseDto[] analyzeText(MultipartFile file, User user) {
        log.info("Start analyze processing for file: {} in Service", file.getOriginalFilename());
        String textContent = extractor.extractTextFromFile(file);
        log.info("Text content extract success for file: {}", file.getOriginalFilename());
        // Конструирование объекта запроса на анализ текста в PyModel
        TextRequestDto requestDto = TextRequestDto.builder()
                .text(textContent)
                .build();

        // Отправка запроса в PyModel клиент на анализ текста
        SentenceResponseDto[] response = client.analyzeText(requestDto);
//        ObjectMapper objectMapper = new ObjectMapper();
//        SentenceResponseDto[] response;
//        try {
//            response = objectMapper.readValue(MockController.getMockAnalyzeResponse(), SentenceResponseDto[].class);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
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
        log.info("Save text: {} to table - texts", text);
        text = textRepository.save(text); // Сохраняем и получаем сгенерированный ID

        // Предзагружаем все POS-теги и синтаксические роли в карты для быстрого доступа
        Map<String, PosTag> posTagMap = posTagRepository.findAll()
                .stream()
                .collect(Collectors.toMap(PosTag::getCode, Function.identity()));
        log.info("POS tags from DB: {}", posTagMap);

        Map<String, SyntacticRole> syntacticRoleMap = syntacticRoleRepository.findAll()
                .stream()
                .collect(Collectors.toMap(SyntacticRole::getCode, Function.identity()));
        log.info("Syntactic roles from DB: {}", syntacticRoleMap);

        // Обработка и сохранение предложений и слов
        int sentenceNumber = 1;
        List<Sentence> sentences = new ArrayList<>();
        List<Word> words = new ArrayList<>();

        log.info("Start save processing for response. Total sentences for saving: {}", response.length);
        for (SentenceResponseDto sentenceDto : response) {
            log.info("Processing for sentence: {}", sentenceDto);
            // Создаем сущность предложения
            Sentence sentence = new Sentence();
            sentence.setText(text);
            sentence.setContent(sentenceDto.getSentence());
            sentence.setSentenceNumber(sentenceNumber++);
            sentences.add(sentence);
            log.info("Sentence entity created: {}", sentence);

            // Обработка слов в предложении
            List<String> wordTexts = sentenceDto.getWords();
            log.info("Init words list: {}", wordTexts);
            List<String> lemmas = sentenceDto.getLemmas();
            log.info("Init lemmas list: {}", lemmas);
            List<String> posTags = sentenceDto.getPosTags();
            log.info("Init pos tags list: {}", posTags);
            List<String> depTags = sentenceDto.getDepTags();
            log.info("Init dep tags list: {}", depTags);

            for (int i = 0; i < wordTexts.size(); i++) {
                log.info("Start processing for word[{}]", i);
                Word word = new Word();
                word.setSentence(sentence);
                word.setWordText(wordTexts.get(i));
                word.setLemma(lemmas.get(i));
                log.info("Word entity created: {}", word);

                // Установка POS-тега
                String posTagCode = posTags.get(i);
                PosTag posTag = posTagMap.get(posTagCode);
                if (posTag == null) {
                    // Если POS-тег не найден, создаем новый (опционально)
                    posTag = new PosTag();
                    posTag.setCode(posTagCode);
                    posTag.setDescription(posTagCode);
                    posTag = posTagRepository.save(posTag);
                    posTagMap.put(posTagCode, posTag);
                }
                word.setPosTag(posTag);
                log.info("Set pos tag for word: {}", word);

                // Установка синтаксической роли
                String depTagCode = depTags.get(i);
                SyntacticRole syntacticRole = syntacticRoleMap.get(depTagCode);
                if (syntacticRole == null) {
                    // Если синтаксическая роль не найдена, создаем новую (опционально)
                    syntacticRole = new SyntacticRole();
                    syntacticRole.setCode(depTagCode);
                    syntacticRole.setDescription(depTagCode);
                    syntacticRole = syntacticRoleRepository.save(syntacticRole);
                    syntacticRoleMap.put(depTagCode, syntacticRole);
                }
                word.setSyntacticRole(syntacticRole);
                log.info("Set syntactic role for word: {}", word);

                words.add(word);
            }
        }

        log.info("Saving all sentence entities: {}", sentences);
        // Пакетное сохранение предложений
        sentenceRepository.saveAll(sentences);

        log.info("Saving all words entities: {}", words);
        // Пакетное сохранение слов
        wordRepository.saveAll(words);

        log.info("All service logic finished, then return response to FRONT");
        return response;
    }
}
