package ru.tsel.demo.configuration;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tsel.demo.repository.ChatRepository;
import ru.tsel.demo.springai.PostgresChatMemory;
import ru.tsel.demo.utils.PromptConstants;

@Configuration
public class SpringAiConfiguration {

	@Bean
	public ChatClient chatClient(
		ChatClient.Builder builder,
		@Qualifier("postgresChatMemoryAdvisor") Advisor postgresMemoryAdvisor,
		@Qualifier("loggerAdviser") Advisor loggerAdviser,
		@Qualifier("ragAdvisor") Advisor ragAdvisor
	) {
		return builder
			.defaultAdvisors(
				postgresMemoryAdvisor,
				loggerAdviser,
				ragAdvisor,
				loggerAdviser
			)
			.defaultOptions(
				OllamaOptions.builder()
					.temperature(0.3)
					.topP(0.7)
					.topK(20)
					.repeatPenalty(1.1)
					.build()
			)
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
	@Qualifier("ragAdvisor")
	public Advisor getRagAdviser(VectorStore vectorStore) {
		return QuestionAnswerAdvisor
			.builder(vectorStore)
			.promptTemplate(PromptConstants.MY_PROMPT_TEMPLATE)
			.searchRequest(
				SearchRequest.builder()
					.topK(4)
					.similarityThreshold(0.4)
					.build()
			).build();
	}

	@Bean
	@Qualifier("loggerAdviser")
	public Advisor logAdviser() {
		return SimpleLoggerAdvisor.builder()
			.build();
	}

	@Bean
	@Qualifier("postgresChatMemory")
	public ChatMemory postgresChatMemory(ChatRepository chatRepository) {
		return PostgresChatMemory.builder()
			.maxMessages(20)
			.chatRepository(chatRepository)
			.build();
	}
}
