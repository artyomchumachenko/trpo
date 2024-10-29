package ru.mai.trpo.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "sentences")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sentence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sentence_id")
    private Long sentenceId;

    @ManyToOne
    @JoinColumn(name = "text_id", nullable = false)
    private Text text;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "sentence_number", nullable = false)
    private Integer sentenceNumber;

    @OneToMany(mappedBy = "sentence", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Word> words;
}
