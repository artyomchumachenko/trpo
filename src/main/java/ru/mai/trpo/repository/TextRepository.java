package ru.mai.trpo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.mai.trpo.model.Text;

public interface TextRepository extends JpaRepository<Text, Long> {
    List<Text> findByUserUsername(String username);
}
