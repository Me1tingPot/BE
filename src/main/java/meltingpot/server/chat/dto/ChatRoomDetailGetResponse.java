package meltingpot.server.chat.dto;

import meltingpot.server.domain.entity.party.Party;

public record ChatRoomDetailGetResponse(
        String imageKey,
        String title,
        Integer userCnt
) {
    public static ChatRoomDetailGetResponse of(Party party, int userCnt) {
        return new ChatRoomDetailGetResponse(
                // [CHECK] 프로필 이미지
                party.getUser().getProfileImages(),
                party.getPartySubject(),
                userCnt
        );
    }
}
