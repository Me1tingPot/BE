package meltingpot.server.chat.dto;

public record ChatMessageCreateRequest(
        Long userId,
        String content,
        Long chatRoomId
) {
}
