package ru.mai.trpo.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import ru.mai.trpo.dto.statistic.SyntacticRoleCountDto;
import ru.mai.trpo.dto.statistic.WordStatisticsDto;
import ru.mai.trpo.repository.WordRepository;

import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WordStatisticsService {

    private final WordRepository wordRepository;

    public List<WordStatisticsDto> getWordStatistics() {
        List<Tuple> rawStatistics = wordRepository.findRawWordStatistics();
        return mapToWordStatisticsDto(rawStatistics);
    }

    public List<WordStatisticsDto> getWordStatisticsByUsername(String username) {
        List<Tuple> rawStatistics = wordRepository.findWordStatisticsByUsername(username);
        return mapToWordStatisticsDto(rawStatistics);
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
                .collect(Collectors.toList());
    }
}
