package ru.tsel.demo.entity;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatMessageRole {

	USER("user"),

	ASSISTANT("assistant");

	private final String roleName;

	public static ChatMessageRole of(String roleName) {
		return Arrays.stream(ChatMessageRole.values())
			.filter(role -> role.getRoleName().equals(roleName))
			.findFirst()
			.orElse(null);
	}
}
