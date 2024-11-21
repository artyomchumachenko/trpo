package ru.mai.trpo.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "pos_tags")
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PosTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pos_tag_id")
    private Long posTagId;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "description", nullable = false)
    private String description;

    @OneToMany(mappedBy = "posTag")
    @ToString.Exclude
    private List<Word> words;
}
