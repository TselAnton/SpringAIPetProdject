package ru.tsel.demo.springai;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.transaction.annotation.Transactional;
import ru.tsel.demo.entity.Chat;
import ru.tsel.demo.entity.ChatMessage;
import ru.tsel.demo.repository.ChatRepository;
import ru.tsel.demo.utils.ChatMessageConverter;

@Builder
@RequiredArgsConstructor
public class PostgresChatMemory implements ChatMemory {

	private final ChatRepository chatRepository;
	private final int maxMessages;

	@Override
	@Transactional
	public void add(String chatId, List<Message> messages) {
		Chat chat = getChat(chatId);
		chat.getHistory().addAll(
			messages.stream()
				.map(ChatMessageConverter::convertToDbMessage)
				.toList()
		);
		chatRepository.save(chat);
	}

	@Override
	public List<Message> get(String chatId) {
		return getChat(chatId)
			.getHistory()
			.stream()
			.sorted(Comparator.comparing(ChatMessage::getCreatedAt).reversed())
			.map(ChatMessageConverter::convertToSpringModel)
			.limit(maxMessages)
			.collect(Collectors.toList());
	}

	@Override
	public void add(String chatId, Message message) {
		this.add(chatId, List.of(message));
	}

	@Override
	@Transactional
	public void clear(String chatId) {
		// NOT NEEDED
	}

	private Chat getChat(String conversationId) {
		return chatRepository.findById(UUID.fromString(conversationId))
			.orElseThrow(() -> new RuntimeException("Chat not found by ID [" + conversationId + "]"));
	}
}
