package ru.tsel.demo.chat;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.tsel.demo.service.ChatService;

@RestController
public class StreamingChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping(value = "/chat-stream/{chatId}",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter talkToModel(@PathVariable UUID chatId, @RequestParam("userPrompt") String userPrompt) {
        return chatService.proceedInteractionWithStreaming(chatId,userPrompt);
    }
}
