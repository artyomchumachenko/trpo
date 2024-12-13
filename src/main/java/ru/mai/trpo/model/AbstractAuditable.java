package ru.mai.trpo.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;

/**
 * Абстрактный класс, обеспечивающий базовые аудируемые поля для сущностей.
 * <p>
 * Все сущности, наследуемые от данного класса, будут иметь:
 * <ul>
 *     <li>Уникальный идентификатор {@code id} типа UUID</li>
 *     <li>Время создания {@code createdAt}</li>
 *     <li>Время последнего обновления {@code updatedAt}</li>
 * </ul>
 * Данные поля заполняются автоматически при создании и обновлении сущностей.
 */
@Data
@MappedSuperclass
public abstract class AbstractAuditable {

    /**
     * Уникальный идентификатор сущности.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * Дата и время создания сущности.
     * Заполняется автоматически при сохранении сущности в базу данных.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    /**
     * Дата и время последнего обновления сущности.
     * Заполняется автоматически при обновлении сущности в базе данных.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
