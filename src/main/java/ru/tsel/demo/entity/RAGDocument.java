package ru.tsel.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "tb_document")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RAGDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column
    private String filename;

    @Column
    private String contentHash;

    @Column
    private String documentType;

    @Column
    private int chunkCount;

    @Column
    @CreationTimestamp
    private LocalDateTime loadedAt;
}
