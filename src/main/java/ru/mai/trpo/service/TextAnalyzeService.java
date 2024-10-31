package ru.mai.trpo.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

import lombok.RequiredArgsConstructor;

/**
 * Сервис "бизнес-логики", связанной с анализом текста
 */
@Service
@RequiredArgsConstructor
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
    public SentenceResponseDto[] analyzeText(MultipartFile file) {
        String textContent = extractor.extractTextFromFile(file);
        // Конструирование объекта запроса на анализ текста в PyModel
        TextRequestDto requestDto = TextRequestDto.builder()
                .text(textContent)
                .build();

        // Отправка запроса в PyModel клиент на анализ текста
        SentenceResponseDto[] response = client.analyzeText(requestDto);

        // Сохраняем информацию о тексте
        Text text = new Text();
        text.setFileName(file.getOriginalFilename());
        try {
            text.setContent(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        text.setUploadDate(LocalDateTime.now());
        text = textRepository.save(text); // Сохраняем и получаем сгенерированный ID

        // Предзагружаем все POS-теги и синтаксические роли в карты для быстрого доступа
        Map<String, PosTag> posTagMap = posTagRepository.findAll()
                .stream()
                .collect(Collectors.toMap(PosTag::getCode, Function.identity()));

        Map<String, SyntacticRole> syntacticRoleMap = syntacticRoleRepository.findAll()
                .stream()
                .collect(Collectors.toMap(SyntacticRole::getCode, Function.identity()));

        // Обработка и сохранение предложений и слов
        int sentenceNumber = 1;
        List<Sentence> sentences = new ArrayList<>();
        List<Word> words = new ArrayList<>();

        for (SentenceResponseDto sentenceDto : response) {
            // Создаем сущность предложения
            Sentence sentence = new Sentence();
            sentence.setText(text);
            sentence.setContent(sentenceDto.getSentence());
            sentence.setSentenceNumber(sentenceNumber++);
            sentences.add(sentence);

            // Обработка слов в предложении
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
                    // Если POS-тег не найден, создаем новый (опционально)
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
                    // Если синтаксическая роль не найдена, создаем новую (опционально)
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

        // Пакетное сохранение предложений
        sentenceRepository.saveAll(sentences);

        // Пакетное сохранение слов
        wordRepository.saveAll(words);

        return response;
    }
}
