package meltingpot.server.chat.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import meltingpot.server.chat.dto.ChatMessageCreateResponse;
import meltingpot.server.chat.dto.ChatMessageCreateRequest;
import meltingpot.server.chat.service.ChatService;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="chat-controller", description = "채팅 기능 API")
@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @MessageMapping("/chat/rooms/{chatRoomId}/send")
    @SendTo("/topic/public/rooms/{chatRoomId}")
    public ChatMessageCreateResponse sendMessage(
            @DestinationVariable("chatRoomId") Long chatRoomId,
            @Payload ChatMessageCreateRequest request,
            @Header("Authorization") String Authorization) {

        return chatService.createChatMessage(chatRoomId, request);
    }
}
