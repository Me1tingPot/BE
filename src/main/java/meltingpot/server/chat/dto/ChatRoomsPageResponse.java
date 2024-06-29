package meltingpot.server.chat.dto;

import meltingpot.server.domain.entity.chat.ChatRoomUser;
import org.springframework.data.domain.Slice;

import java.util.List;

public record ChatRoomsPageResponse(
        List<ChatRoomGetResponse> chatRoomGetResponseList,
        Boolean isFirst,
        Boolean hasNext
) {
    public static ChatRoomsPageResponse from(Slice<ChatRoomUser> chatRoomUserSlice) {
        return new ChatRoomsPageResponse(
                chatRoomUserSlice.stream().map(ChatRoomGetResponse::from).toList(),
                chatRoomUserSlice.isFirst(),
                chatRoomUserSlice.hasNext()
        );
    }
}
