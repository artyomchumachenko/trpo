package ru.mai.trpo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import ru.mai.trpo.model.Sentence;
import ru.mai.trpo.model.Word;

import jakarta.persistence.Tuple;

public interface WordRepository extends JpaRepository<Word, Long> {
    @Query("""
        SELECT w.wordText AS wordText, sr.description AS roleDescription, COUNT(w.wordId) AS count
        FROM Word w
        JOIN w.syntacticRole sr
        JOIN w.sentence s
        JOIN s.text t
        JOIN t.user u
        WHERE u.username = :username
        GROUP BY w.wordText, sr.description
        """)
    List<Tuple> findWordStatisticsByUsername(@Param("username") String username);

    @Query("""
        SELECT w.wordText AS wordText, sr.description AS roleDescription, COUNT(w.wordId) AS count
        FROM Word w
        JOIN w.syntacticRole sr
        GROUP BY w.wordText, sr.description
        """)
    List<Tuple> findRawWordStatistics();
}
