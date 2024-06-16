package meltingpot.server.chat.dto;

public record UnreadChatMessageSendDTO(
        Long chatRoomId,
        int unreadMessageCnt
) {
}
