package meltingpot.server.chat.dto;

import meltingpot.server.domain.entity.AccountProfileImage;
import meltingpot.server.domain.entity.chat.ChatRoomUser;
import meltingpot.server.domain.entity.party.enums.PartyStatus;

import java.time.LocalDateTime;

public record ChatRoomGetResponse(
        Long chatRoomId,
        String leaderName,
        String imageKey,
        String partySubject,
        PartyStatus partyStatus,
        String partyLocationAddress,
        LocalDateTime partyStartTime,
        int userCnt,
        int messageCnt
) {
    public static ChatRoomGetResponse from(ChatRoomUser chatRoomUser) {
        String thumbnailImageKey = chatRoomUser.getChatRoom().getParty().getAccount().getProfileImages().stream()
                .filter(AccountProfileImage::isThumbnail)
                .findFirst()
                .map(AccountProfileImage::getImageKey)
                .orElse(null);

        return new ChatRoomGetResponse(
                chatRoomUser.getChatRoom().getParty().getChatRoom().getId(),
                chatRoomUser.getChatRoom().getParty().getAccount().getUsername(),
                thumbnailImageKey,
                chatRoomUser.getChatRoom().getParty().getPartySubject(),
                chatRoomUser.getChatRoom().getParty().getPartyStatus(),
                chatRoomUser.getChatRoom().getParty().getPartyLocationAddress(),
                chatRoomUser.getChatRoom().getParty().getPartyStartTime(),
                chatRoomUser.getChatRoom().getChatRoomUserList().size(),
                chatRoomUser.getUnreadMessageCnt()
        );
    }
}
