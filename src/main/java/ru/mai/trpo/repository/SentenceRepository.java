package ru.mai.trpo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import ru.mai.trpo.model.Sentence;
import ru.mai.trpo.model.Text;

public interface SentenceRepository extends JpaRepository<Sentence, Long> {
    List<Sentence> findByText(Text text);
}
