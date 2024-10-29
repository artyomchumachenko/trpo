package ru.mai.trpo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import ru.mai.trpo.model.Sentence;
import ru.mai.trpo.model.Word;

public interface WordRepository extends JpaRepository<Word, Long> {
    List<Word> findBySentence(Sentence sentence);
}
