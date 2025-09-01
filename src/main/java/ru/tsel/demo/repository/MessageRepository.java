package ru.tsel.demo.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.tsel.demo.entity.Message;

public interface MessageRepository extends JpaRepository<Message, UUID> {
}
