package meltingpot.server.chat.dto;

import meltingpot.server.domain.entity.AccountProfileImage;
import meltingpot.server.domain.entity.party.Party;

public record ChatRoomDetailGetResponse(
        String imageKey,
        String title,
        Integer userCnt
) {
    public static ChatRoomDetailGetResponse of(Party party, int userCnt) {
        String thumbnailImageKey = party.getAccount().getProfileImages().stream()
                .filter(AccountProfileImage::isTumbnail)
                .map(AccountProfileImage::getImageKey)
                .findFirst()
                .orElse(null);

        return new ChatRoomDetailGetResponse(
                // [CHECK] 프로필 이미지
                thumbnailImageKey,
                party.getPartySubject(),
                userCnt
        );
    }
}
