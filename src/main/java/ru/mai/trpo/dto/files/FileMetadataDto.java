package ru.mai.trpo.dto.files;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileMetadataDto {
    private Long id; // Идентификатор файла
    private String fileName; // Название файла
    private LocalDateTime uploadDate; // Дата загрузки
}
