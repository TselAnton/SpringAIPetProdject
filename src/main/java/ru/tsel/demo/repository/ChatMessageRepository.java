package ru.tsel.demo.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.tsel.demo.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
}
