package ru.tsel.demo.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.tsel.demo.entity.Chat;

public interface ChatRepository extends JpaRepository<Chat, UUID> {
}
