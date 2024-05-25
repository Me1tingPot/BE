package meltingpot.server.chat.controller;

import lombok.RequiredArgsConstructor;
import meltingpot.server.chat.dto.ChatMessageCreateResponse;
import meltingpot.server.chat.dto.ChatMessageCreateRequest;
import meltingpot.server.chat.service.ChatService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @MessageMapping("/chat/rooms/{chatRoomId}/send")    // <- 해당 경로로 들어오면 메서드 호출
    @SendTo("/topic/public/rooms/{chatRoomId}") // 구독 중인 장소로 메세지 전달
    public ChatMessageCreateResponse sendMessage(
            @DestinationVariable Long chatRoomId,
            @Payload ChatMessageCreateRequest request) {
        ChatMessageCreateResponse response = chatService.createChatMessage(chatRoomId, request);
        return response;
    }
}
