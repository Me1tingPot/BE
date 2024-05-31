package meltingpot.server.chat.dto;

import lombok.Builder;
import meltingpot.server.domain.entity.chat.ChatMessage;
import org.springframework.data.domain.Slice;

import java.util.List;

@Builder
public record ChatMessagePageResponse(
        List<ChatMessageGetResponse> chatMessageGetResponseList,
        Boolean isFirst,
        Boolean hasNext
) {
    public static ChatMessagePageResponse from(Slice<ChatMessage> chatMessagesSlice) {
        return new ChatMessagePageResponse(
                chatMessagesSlice.stream().map(ChatMessageGetResponse::from).toList(),
                chatMessagesSlice.isFirst(),
                chatMessagesSlice.hasNext()
        );
    }
}
