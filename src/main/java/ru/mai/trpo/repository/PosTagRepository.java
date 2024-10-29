package ru.mai.trpo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.mai.trpo.model.PosTag;

public interface PosTagRepository extends JpaRepository<PosTag, Long> {
}
