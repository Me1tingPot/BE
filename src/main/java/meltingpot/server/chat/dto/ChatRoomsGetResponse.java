package meltingpot.server.chat.dto;

import java.time.LocalDateTime;

public record ChatRoomsGetResponse(
        Long chatRoomId,
        String leaderName,
        String imageKey,
        String partySubject,
        String partyStatus,
        String partyLocationAddress,
        LocalDateTime partyDate,
        Long userCnt,
        Long alarmCnt
) {
}
