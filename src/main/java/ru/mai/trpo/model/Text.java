package ru.mai.trpo.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "texts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Text {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "text_id")
    private Long textId;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "content", nullable = false, columnDefinition="bytea")
    private byte[] content;

    @Column(name = "upload_date", nullable = false)
    private LocalDateTime uploadDate = LocalDateTime.now();

    @OneToMany(mappedBy = "text", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sentence> sentences;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
