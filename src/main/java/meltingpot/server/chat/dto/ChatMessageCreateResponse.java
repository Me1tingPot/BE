package meltingpot.server.chat.dto;

import lombok.Builder;

@Builder
public record ChatMessageCreateResponse(
        Long chatId
) {
}
