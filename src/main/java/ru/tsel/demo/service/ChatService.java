package ru.tsel.demo.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.tsel.demo.entity.Chat;
import ru.tsel.demo.repository.ChatRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatClient chatClient;
    private final ChatRepository chatRepository;

    @Autowired
    private ChatService selfInjection;

    public List<Chat> getAllChats() {
        return chatRepository.findAll(
            Sort.by(Sort.Direction.DESC, "createdAt")
        );
    }

    public Chat createNewChat(String title) {
         Chat chat = chatRepository.save(
            Chat.builder()
                .title(title)
                .build()
        );
        log.info("Created chat ID {}", chat.getId());
        return chat;
    }

    public Chat getChat(UUID chatId) {
        return chatRepository.findById(chatId)
            .orElseThrow(() -> new RuntimeException("Chat not found"));
    }

    public void deleteChat(UUID chatId) {
        log.info("Delete chat by ID {}", chatId);
        chatRepository.deleteById(chatId);
    }

    @SneakyThrows
    public SseEmitter proceedInteractionWithStreaming(UUID chatId, String userPrompt) {
        SseEmitter sseEmitter = new SseEmitter(0L);
        chatClient.prompt()
            .advisors(advisorSpec -> advisorSpec.param(
                ChatMemory.CONVERSATION_ID, chatId
            ))
            .user(userPrompt)
            .stream()
            .chatResponse()
            .subscribe(
                response -> getSend(response, sseEmitter),
                sseEmitter::completeWithError,
                sseEmitter::complete
            );
        return sseEmitter;
    }

    @SneakyThrows
    private static void getSend(ChatResponse response, SseEmitter sseEmitter) {
        AssistantMessage assistantMessage = response.getResult().getOutput();
        sseEmitter.send(assistantMessage);
    }
}
