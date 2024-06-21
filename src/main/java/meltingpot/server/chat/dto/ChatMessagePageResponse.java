package meltingpot.server.chat.dto;

import meltingpot.server.domain.entity.AccountProfileImage;
import meltingpot.server.domain.entity.chat.ChatMessage;
import meltingpot.server.domain.entity.chat.ChatRoom;
import org.springframework.data.domain.Slice;

import java.util.List;

public record ChatMessagePageResponse(
        List<ChatMessageGetResponse> chatMessageGetResponseList,
        Long chatRoomId,
        String hostImageKey,
        String title,
        int memberCnt,
        Boolean isFirst,
        Boolean hasNext
) {
    public static ChatMessagePageResponse from(Slice<ChatMessage> chatMessagesSlice, ChatRoom chatRoom) {
        String thumbnailImageKey = chatRoom.getParty().getAccount().getProfileImages().stream()
                .filter(AccountProfileImage::isThumbnail)
                .map(AccountProfileImage::getImageKey)
                .findFirst()
                .orElse(null);

        return new ChatMessagePageResponse(
                chatMessagesSlice.stream().map(ChatMessageGetResponse::from).toList(),
                chatRoom.getId(),
                thumbnailImageKey,
                chatRoom.getParty().getPartySubject(),
                chatRoom.getParty().getPartyParticipants().size(),
                chatMessagesSlice.isFirst(),
                chatMessagesSlice.hasNext()
        );
    }
}
