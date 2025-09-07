package ru.tsel.demo.springai.advisors;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.stereotype.Component;

@Builder
@RequiredArgsConstructor
public class ExpansionQueryAdvisor implements BaseAdvisor {

	private final Integer order;

	@Override
	public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
		String userQuestion = chatClientRequest.prompt().getUserMessage().getText();
		return chatClientRequest;
	}

	@Override
	public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
		return chatClientResponse;
	}

	@Override
	public int getOrder() {
		return order != null ? order : Integer.MIN_VALUE + 500;
	}
}
