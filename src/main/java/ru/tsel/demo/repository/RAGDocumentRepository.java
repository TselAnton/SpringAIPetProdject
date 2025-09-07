package ru.tsel.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tsel.demo.entity.RAGDocument;

public interface RAGDocumentRepository extends JpaRepository<RAGDocument, Long> {

    boolean existsByFilenameAndContentHash(String filename, String contentHash);
}
