package meltingpot.server.chat.dto;

public record ChatRoomDetailGetResponse(
        String imageKey,
        String title,
        Integer userCnt
) {
}
