package meltingpot.server.chat.dto;

import meltingpot.server.domain.entity.party.Party;

import java.time.LocalDateTime;

public record ChatRoomsGetResponse(
        Long chatRoomId,
        String leaderName,
        String imageKey,
        String partySubject,
        String partyStatus,
        String partyLocationAddress,
        LocalDateTime partyStartTime,
        int userCnt,
        int messageCnt
) {
    // [CHECK] 유저 프로필 이미지
    public static ChatRoomsGetResponse of(Party party, int userCnt, int messageCnt) {
//        return new ChatRoomsGetResponse(
//                party.getChatRoom().getId(),
//                party.getUser().getUsername(),
//                party.getUser().getProfileImages(),
//                party.getPartySubject(),
//                party.getPartyLocationAddress(),
//                party.getPartyStartTime(),
//                userCnt,
//                messageCnt
//        );
        return null;
    }
}
