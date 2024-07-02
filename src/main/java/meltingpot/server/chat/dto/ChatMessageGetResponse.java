package meltingpot.server.chat.dto;

import meltingpot.server.domain.entity.chat.ChatMessage;
import meltingpot.server.domain.entity.chat.enums.Role;

public record ChatMessageGetResponse(
        Long chatMessageId,
        String content,
        Role memberRole,
        String memberName,
        String imageKey
) {
    public static ChatMessageGetResponse from(ChatMessage chatMessage, String thumbnailUrl) {
        return new ChatMessageGetResponse(
                chatMessage.getId(),
                chatMessage.getContent(),
                chatMessage.getRole(),
                chatMessage.getAccount().getUsername(),
                thumbnailUrl
        );
    }
}
