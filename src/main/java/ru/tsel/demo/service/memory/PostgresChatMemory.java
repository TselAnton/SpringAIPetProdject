package ru.tsel.demo.service.memory;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.tsel.demo.entity.Chat;
import ru.tsel.demo.entity.MessageRole;
import ru.tsel.demo.repository.ChatRepository;

@Component
@RequiredArgsConstructor
public class PostgresChatMemory implements ChatMemory {

	private final ChatRepository chatRepository;

	@Override
	@Transactional
	public void add(String conversationId, List<Message> messages) {
		Chat chat = getChat(conversationId);

		chat.getHistory().addAll(
			messages.stream()
				.map(this::convertMessageToDbModel)
				.toList()
		);
		chatRepository.save(chat);
	}

	@Override
	@Transactional
	public List<Message> get(String conversationId, int lastN) {
		return getChat(conversationId)
			.getHistory()
			.stream()
			.sorted(Comparator.comparing(ru.tsel.demo.entity.Message::getCreatedAt).reversed())
			.limit(lastN)
//			.sorted(Comparator.comparing(ru.tsel.demo.entity.Message::getCreatedAt))
			.map(this::convertMessageToSpringModel)
			.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void clear(String conversationId) {
		chatRepository.deleteById(UUID.fromString(conversationId));
	}

	private Chat getChat(String conversationId) {
		return chatRepository.findById(UUID.fromString(conversationId))
			.orElseThrow(() -> new RuntimeException("Chat not found by ID [" + conversationId + "]"));
	}

	private Message convertMessageToSpringModel(ru.tsel.demo.entity.Message message) {
		return switch (message.getRole()) {
			case USER -> new UserMessage(message.getContent());
			case ASSISTANT -> new AssistantMessage(message.getContent());
		};
	}

	private ru.tsel.demo.entity.Message convertMessageToDbModel(Message message) {
		return ru.tsel.demo.entity.Message.builder()
			.content(message.getText())
			.role(MessageType.USER == message.getMessageType() ? MessageRole.USER : MessageRole.ASSISTANT)
			.build();
	}
}
