package ru.mai.trpo.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "syntactic_roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SyntacticRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "syntactic_role_id")
    private Long syntacticRoleId;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "description", nullable = false)
    private String description;

    @OneToMany(mappedBy = "syntacticRole")
    private List<Word> words;
}
