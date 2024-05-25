package meltingpot.server.chat.service;

import lombok.RequiredArgsConstructor;
import meltingpot.server.chat.dto.ChatMessageGetResponse;
import meltingpot.server.chat.dto.ChatMessageListQuery;
import meltingpot.server.chat.dto.ChatRoomDetailGetResponse;
import meltingpot.server.chat.dto.ChatRoomsGetResponse;
import meltingpot.server.domain.entity.chat.ChatRoom;
import meltingpot.server.domain.entity.party.Party;
import meltingpot.server.domain.repository.chat.ChatMessageRepository;
import meltingpot.server.domain.repository.chat.ChatRoomRepository;
import meltingpot.server.domain.repository.chat.ChatRoomUserRepository;
import meltingpot.server.domain.repository.party.PartyRepository;
import meltingpot.server.util.PageResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomQueryService {
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final PartyRepository partyRepository;

    public ChatRoomDetailGetResponse getRoomDetail(Long chatRoomId) {
        Party party = partyRepository.findByChatRoomId(chatRoomId);
        return ChatRoomDetailGetResponse.of(party, chatRoomUserRepository.countChatRoomUsersByChatRoomId(chatRoomId));
    }

    // [CHECK]
    public PageResponse<List<ChatMessageGetResponse>> getChatMessage(ChatMessageListQuery query) {
        ChatRoom chatRoom = chatRoomRepository.findById(query.chatRoomId())
                .orElseThrow(() -> new IllegalArgumentException("ChatRoom not found"));
        PageRequest pageRequest = PageRequest.of(query.page(), query.size(), Sort.by("chatMessageId").descending());
        List<ChatMessageGetResponse> chatMessages = chatMessageRepository.findAllByChatRoom(chatRoom, pageRequest)
                .stream().map(ChatMessageGetResponse::from).toList();

        // return PageResponse.of(chatMessages, pageRequest);
        return null;
    }

    public PageResponse<List<ChatRoomsGetResponse>> getChatRooms() {
        return null;
    }
}
