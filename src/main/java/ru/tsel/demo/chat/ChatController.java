package ru.tsel.demo.chat;

import jakarta.websocket.server.PathParam;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.tsel.demo.entity.Chat;
import ru.tsel.demo.service.ChatService;

@Controller
public class ChatController {

	@Autowired
	private ChatService chatService;

	@GetMapping("/")
	public String mainPage(ModelMap model) {
		model.addAttribute("chats", chatService.getAllChats());
		return "chat";
	}

	@GetMapping("/chat/{chatId}")
	public String showChat(@PathVariable UUID chatId, ModelMap model) {
		model.addAttribute("chats", chatService.getAllChats());
		model.addAttribute("chat", chatService.getChat(chatId));
		return "chat";

	}

	@PostMapping("/chat/new")
	public String newChat(@RequestParam String title) {
		Chat chat = chatService.createNewChat(title);
		return "redirect:/chat/" + chat.getId();
	}

	@PostMapping("chat/{chatId}/delete")
	public String deleteChat(@PathVariable UUID chatId) {
		chatService.deleteChat(chatId);
		return "redirect:/";
	}
}
