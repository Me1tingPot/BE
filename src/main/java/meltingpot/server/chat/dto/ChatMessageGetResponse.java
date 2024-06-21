package meltingpot.server.chat.dto;

import meltingpot.server.domain.entity.AccountProfileImage;
import meltingpot.server.domain.entity.chat.ChatMessage;
import meltingpot.server.domain.entity.chat.enums.Role;
import org.springframework.data.domain.Slice;

public record ChatMessageGetResponse(
        Long chatMessageId,
        String content,
        Role memberRole,
        String memberName,
        String imageKey
) {
    public static ChatMessageGetResponse from(ChatMessage chatMessage) {
        String thumbnailImageKey = chatMessage.getChatRoom().getParty().getAccount().getProfileImages().stream()
                .filter(AccountProfileImage::isThumbnail)
                .map(AccountProfileImage::getImageKey)
                .findFirst()
                .orElse(null);

        return new ChatMessageGetResponse(
                chatMessage.getId(),
                chatMessage.getContent(),
                chatMessage.getRole(),
                chatMessage.getAccount().getUsername(),
                thumbnailImageKey
        );
    }
}
