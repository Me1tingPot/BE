package meltingpot.server.chat.dto;

public record ChatMessageSendRequest(
        String content,
        Long chatRoomId
) {
}
