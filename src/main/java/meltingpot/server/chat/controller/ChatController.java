package meltingpot.server.chat.controller;

import lombok.RequiredArgsConstructor;
import meltingpot.server.chat.dto.ChatMessageCreateResponse;
import meltingpot.server.chat.dto.ChatMessageCreateRequest;
import meltingpot.server.chat.service.ChatService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @MessageMapping("/chat/rooms/{chatRoomId}/send")
    @SendTo("/topic/public/rooms/{chatRoomId}")
    public ChatMessageCreateResponse sendMessage(
            @DestinationVariable Long chatRoomId,
            @Payload ChatMessageCreateRequest request) {
        return chatService.createChatMessage(chatRoomId, request);
    }
}
