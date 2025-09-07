package ru.tsel.demo.utils;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import ru.tsel.demo.entity.ChatMessage;
import ru.tsel.demo.entity.ChatMessageRole;

public final class ChatMessageConverter {

	public static Message convertToSpringModel(ChatMessage chatMessage) {
		return switch (chatMessage.getRole()) {
			case USER -> new UserMessage(chatMessage.getContent());
			case ASSISTANT -> new AssistantMessage(chatMessage.getContent());
		};
	}

	public static ChatMessage convertToDbMessage(Message message) {
		return ChatMessage.builder()
			.content(message.getText())
			.role(MessageType.USER == message.getMessageType() ? ChatMessageRole.USER : ChatMessageRole.ASSISTANT)
			.build();
	}
}
