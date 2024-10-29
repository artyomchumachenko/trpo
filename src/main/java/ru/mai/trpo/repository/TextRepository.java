package ru.mai.trpo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.mai.trpo.model.Text;

public interface TextRepository extends JpaRepository<Text, Long> {
}
