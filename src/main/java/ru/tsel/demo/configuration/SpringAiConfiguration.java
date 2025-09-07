package ru.tsel.demo.configuration;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tsel.demo.repository.ChatRepository;
import ru.tsel.demo.springai.PostgresChatMemory;

@Configuration
public class SpringAiConfiguration {

	@Autowired
	private ChatRepository chatRepository;

	@Bean
	public ChatClient chatClient(
		ChatClient.Builder builder,
		@Qualifier("postgresChatMemoryAdvisor") Advisor advisor
	) {
		return builder
			.defaultAdvisors(advisor)
			.build();
	}

	@Bean
	@Qualifier("postgresChatMemoryAdvisor")
	public Advisor postgesChatMemoryAdvisor(@Qualifier("postgresChatMemory") ChatMemory chatMemory) {
		return MessageChatMemoryAdvisor
			.builder(chatMemory)
			.build();
	}

	@Bean
	@Qualifier("postgresChatMemory")
	public ChatMemory postgresChatMemory(ChatRepository chatRepository) {
		return PostgresChatMemory.builder()
			.maxMessages(2)
			.chatRepository(chatRepository)
			.build();
	}
}
