package meltingpot.server.chat.dto;

import meltingpot.server.domain.entity.chat.ChatMessage;
import meltingpot.server.domain.entity.chat.enums.Role;
import org.springframework.data.domain.Slice;

public record ChatMessageGetResponse(
        Long chatId,
        String content,
        Role role
) {
    public static ChatMessageGetResponse from(ChatMessage chatMessage) {
        return new ChatMessageGetResponse(
                chatMessage.getId(),
                chatMessage.getContent(),
                chatMessage.getRole()
        );
    }
}
