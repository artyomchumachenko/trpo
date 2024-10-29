package ru.mai.trpo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.mai.trpo.model.SyntacticRole;

public interface SyntacticRoleRepository extends JpaRepository<SyntacticRole, Long> {
}
