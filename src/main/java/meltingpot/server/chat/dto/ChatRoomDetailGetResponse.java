package meltingpot.server.chat.dto;

import meltingpot.server.domain.entity.AccountProfileImage;
import meltingpot.server.domain.entity.party.Party;

public record ChatRoomDetailGetResponse(
        String imageKey,
        String title,
        int userCnt
) {
    public static ChatRoomDetailGetResponse from(Party party) {
        String thumbnailImageKey = party.getAccount().getProfileImages().stream()
                .filter(AccountProfileImage::isThumbnail)
                .map(AccountProfileImage::getImageKey)
                .findFirst()
                .orElse(null);

        return new ChatRoomDetailGetResponse(
                thumbnailImageKey,
                party.getPartySubject(),
                party.getPartyParticipants().size()
        );
    }
}
