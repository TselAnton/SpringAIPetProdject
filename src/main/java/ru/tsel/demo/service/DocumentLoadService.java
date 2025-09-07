package ru.tsel.demo.service;

import java.io.FileNotFoundException;
import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import ru.tsel.demo.entity.RAGDocument;
import ru.tsel.demo.repository.RAGDocumentRepository;

@Slf4j
@Service
public class DocumentLoadService implements CommandLineRunner {

    private static final int CHUNK_SIZE = 500;

    @Autowired
    private RAGDocumentRepository documentRepository;

    @Autowired
    private ResourcePatternResolver resolver;

    @Autowired
    private VectorStore vectorStore;

    @SneakyThrows
    public void loadDocuments() {
        try {
            Resource[] resources = resolver.getResources("classpath:/rag_files/**/*.txt");
            if (resources.length == 0) {
                log.info("Not found any resources for load");
                return;
            }

            for (Resource resource : resources) {
                String resourceHashSum = calcContentHash(resource);
                if (documentRepository.existsByFilenameAndContentHash(resource.getFilename(), resourceHashSum)) {
                    log.info("Resource {} already uploaded with hash sum {}", resource.getFilename(), resourceHashSum);
                    continue;
                }

                // Разбиение документа на чанки
                List<Document> documents = new TextReader(resource).get();
                List<Document> chunks = TokenTextSplitter.builder()
                    .withChunkSize(CHUNK_SIZE)
                    .build()
                    .apply(documents);

                // Запись документа в vecor-store (opensearch)
                vectorStore.accept(chunks);

                // Сохранение файла в БД
                documentRepository.save(
                    RAGDocument.builder()
                        .documentType("txt")
                        .chunkCount(chunks.size())
                        .filename(resource.getFilename())
                        .contentHash(resourceHashSum)
                        .build()
                );
            }

        } catch (FileNotFoundException e) {
            log.warn("Not found any files for RAG", e);
        }
    }

    @SneakyThrows
    private String calcContentHash(Resource resource) {
        return DigestUtils.md5DigestAsHex(resource.getInputStream());
    }

    @Override
    public void run(String... args) {
        loadDocuments();
    }
}
