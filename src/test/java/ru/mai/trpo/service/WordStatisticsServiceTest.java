package ru.mai.trpo.service;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ru.mai.trpo.dto.statistic.WordStatisticsDto;
import ru.mai.trpo.repository.WordRepository;

import jakarta.persistence.Tuple;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class WordStatisticsServiceTest {

    @Mock
    private WordRepository wordRepository;

    @InjectMocks
    private WordStatisticsService wordStatisticsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private List<Tuple> mockData() {
        Tuple mockTuple1 = mock(Tuple.class);
        when(mockTuple1.get("wordText", String.class)).thenReturn("hello");
        when(mockTuple1.get("roleDescription", String.class)).thenReturn("Подлежащее");
        when(mockTuple1.get("count", Long.class)).thenReturn(5L);

        Tuple mockTuple2 = mock(Tuple.class);
        when(mockTuple2.get("wordText", String.class)).thenReturn("world");
        when(mockTuple2.get("roleDescription", String.class)).thenReturn("Дополнение");
        when(mockTuple2.get("count", Long.class)).thenReturn(3L);

        return Arrays.asList(mockTuple1, mockTuple2);
    }

    @Test
    void testGetWordStatistics() {
        List<Tuple> mockData = mockData();
        when(wordRepository.findRawWordStatistics()).thenReturn(mockData);

        List<WordStatisticsDto> result = wordStatisticsService.getWordStatistics();

        assertEquals(2, result.size());

        WordStatisticsDto firstWord = result.get(0);
        assertEquals("hello", firstWord.getWordText());
        assertEquals(1, firstWord.getRoles().size());
        assertEquals("Подлежащее", firstWord.getRoles().get(0).getSyntacticRoleDescription());
        assertEquals(5L, firstWord.getRoles().get(0).getCount());

        verify(wordRepository, times(1)).findRawWordStatistics();
    }

    @Test
    void testGetWordStatisticsByUsername() {
        List<Tuple> mockData = mockData();
        when(wordRepository.findWordStatisticsByUsername("john_doe")).thenReturn(mockData);

        List<WordStatisticsDto> result = wordStatisticsService.getWordStatisticsByUsername("john_doe");

        assertEquals(2, result.size());

        WordStatisticsDto firstWord = result.get(0);
        assertEquals("hello", firstWord.getWordText());
        assertEquals(1, firstWord.getRoles().size());
        assertEquals("Подлежащее", firstWord.getRoles().get(0).getSyntacticRoleDescription());
        assertEquals(5L, firstWord.getRoles().get(0).getCount());

        verify(wordRepository, times(1)).findWordStatisticsByUsername("john_doe");
    }

    @Test
    void testGetWordStatistics_SortedAlphabetically() {
        Tuple mockTuple1 = mock(Tuple.class);
        when(mockTuple1.get("wordText", String.class)).thenReturn("apple");
        when(mockTuple1.get("roleDescription", String.class)).thenReturn("Подлежащее");
        when(mockTuple1.get("count", Long.class)).thenReturn(2L);

        Tuple mockTuple2 = mock(Tuple.class);
        when(mockTuple2.get("wordText", String.class)).thenReturn("banana");
        when(mockTuple2.get("roleDescription", String.class)).thenReturn("Дополнение");
        when(mockTuple2.get("count", Long.class)).thenReturn(3L);

        Tuple mockTuple3 = mock(Tuple.class);
        when(mockTuple3.get("wordText", String.class)).thenReturn("apple");
        when(mockTuple3.get("roleDescription", String.class)).thenReturn("Сказуемое");
        when(mockTuple3.get("count", Long.class)).thenReturn(1L);

        when(wordRepository.findRawWordStatistics()).thenReturn(Arrays.asList(mockTuple1, mockTuple2, mockTuple3));

        List<WordStatisticsDto> result = wordStatisticsService.getWordStatistics();

        assertEquals(2, result.size());
        assertEquals("apple", result.get(0).getWordText());
        assertEquals("banana", result.get(1).getWordText());

        verify(wordRepository, times(1)).findRawWordStatistics();
    }
}
