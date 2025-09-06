package ru.tsel.demo.service;

import java.beans.Transient;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.tsel.demo.entity.Chat;
import ru.tsel.demo.entity.Message;
import ru.tsel.demo.entity.MessageRole;
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

    @Transactional
    public void proceedInteraction(UUID chatId, String prompt) {
        selfInjection.addChatEntry(chatId, prompt, MessageRole.USER);
        log.info("User question: {}", prompt);

        try {
            String answer = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

            log.info("Assistant answer: {}", answer);

            selfInjection.addChatEntry(chatId, answer, MessageRole.ASSISTANT);
        } catch (RuntimeException e) {
            log.error("Exception for chat", e);
        }
    }

    @Transactional
    public void addChatEntry(UUID chatId, String prompt, MessageRole messageRole) {
        Chat chat = chatRepository.findById(chatId)
            .orElseThrow(() -> new RuntimeException("Chat not found by ID " + chatId));
        chat
            .getHistory()
            .add(
                Message.builder()
                    .content(prompt)
                    .role(messageRole)
                    .build()
            );
        chatRepository.save(chat);
    }

    @SneakyThrows
    public SseEmitter proceedInteractionWithStreaming(UUID chatId, String userPrompt) {
        selfInjection.addChatEntry(chatId, userPrompt, MessageRole.USER);

        SseEmitter sseEmitter = new SseEmitter(0L);

        StringBuilder responseToken = new StringBuilder();
        chatClient.prompt()
            .user(userPrompt)
            .stream()
            .chatResponse()
            .subscribe(
                response -> {
                    responseToken.append(response.getResult().getOutput().getText());
                    getSend(response, sseEmitter);
                },
                sseEmitter::completeWithError,
                () -> selfInjection.addChatEntry(chatId, responseToken.toString(), MessageRole.ASSISTANT)
            );

        return sseEmitter;
    }

    @SneakyThrows
    private static void getSend(ChatResponse response, SseEmitter sseEmitter) {
        sseEmitter.send(response.getResult().getOutput());
    }
}
