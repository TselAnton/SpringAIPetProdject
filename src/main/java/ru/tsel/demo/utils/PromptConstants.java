package ru.tsel.demo.utils;

import org.springframework.ai.chat.prompt.PromptTemplate;

public final class PromptConstants {

	public static final PromptTemplate MY_PROMPT_TEMPLATE = new PromptTemplate(
		"""
			{query}
		
			Контекст:
			---------------------
			{question_answer_context}
			---------------------
		
			Отвечай только на основе контекста выше. Если информации нет в контексте, сообщи, что не можешь ответить.
		"""
	);
}
