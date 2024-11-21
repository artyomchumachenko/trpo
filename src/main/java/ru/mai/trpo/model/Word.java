package ru.mai.trpo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "words")
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "word_id")
    private Long wordId;

    @ManyToOne
    @JoinColumn(name = "sentence_id", nullable = false)
    @ToString.Exclude
    private Sentence sentence;

    @Column(name = "word_text", nullable = false)
    private String wordText;

    @Column(name = "lemma", nullable = false)
    private String lemma;

    @ManyToOne
    @JoinColumn(name = "pos_tag_id")
    @ToString.Exclude
    private PosTag posTag;

    @ManyToOne
    @JoinColumn(name = "syntactic_role_id")
    @ToString.Exclude
    private SyntacticRole syntacticRole;
}
