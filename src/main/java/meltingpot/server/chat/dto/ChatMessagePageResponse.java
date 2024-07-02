package meltingpot.server.chat.dto;

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
    public static ChatMessagePageResponse from(List<ChatMessageGetResponse> chatMessageGetResponseList, ChatRoom chatRoom, String thumbnailUrl, Slice<ChatMessage> chatMessagesSlice) {
        return new ChatMessagePageResponse(
                chatMessageGetResponseList,
                chatRoom.getId(),
                thumbnailUrl,
                chatRoom.getParty().getPartySubject(),
                chatRoom.getParty().getPartyParticipants().size(),
                chatMessagesSlice.isFirst(),
                chatMessagesSlice.hasNext()
        );
    }
}
