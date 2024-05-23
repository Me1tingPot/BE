package meltingpot.server.chat.service;

import lombok.RequiredArgsConstructor;
import meltingpot.server.chat.dto.ChatMessagesGetResponse;
import meltingpot.server.chat.dto.ChatRoomDetailGetResponse;
import meltingpot.server.chat.dto.ChatRoomsGetResponse;
import meltingpot.server.domain.repository.ChatRepository;
import meltingpot.server.domain.repository.ChatRoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomQueryService {
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;

    public List<ChatRoomsGetResponse> getChatRooms() {
        return null;
    }

    public List<ChatMessagesGetResponse> getChatMessage() {
        return null;
    }

    public ChatRoomDetailGetResponse getChatRoomDetail() {
        return null;
    }
}
