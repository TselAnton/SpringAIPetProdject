package ru.tsel.demo.entity;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageRole {

	USER("user"),

	ASSISTANT("assistant");

	private final String roleName;

	public static MessageRole of(String roleName) {
		return Arrays.stream(MessageRole.values())
			.filter(role -> role.getRoleName().equals(roleName))
			.findFirst()
			.orElse(null);
	}
}
