package meltingpot.server.chat.dto;

import lombok.Builder;

@Builder
public record ChatMessageListQuery(
        Long chatRoomId,
        int page,
        int size
) {
}
