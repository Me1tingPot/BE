package meltingpot.server.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meltingpot.server.chat.dto.*;
import meltingpot.server.domain.entity.chat.ChatMessage;
import meltingpot.server.domain.entity.chat.ChatRoom;
import meltingpot.server.domain.entity.party.Party;
import meltingpot.server.domain.repository.chat.ChatMessageRepository;
import meltingpot.server.domain.repository.chat.ChatRoomRepository;
import meltingpot.server.domain.repository.chat.ChatRoomUserRepository;
import meltingpot.server.domain.repository.party.PartyRepository;
import meltingpot.server.exception.ResourceNotFoundException;
import meltingpot.server.util.PageResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static meltingpot.server.util.ResponseCode.PARTY_NOT_FOUND;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomQueryService {
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final PartyRepository partyRepository;

    public ChatRoomDetailGetResponse getRoomDetail(Long chatRoomId) {
        Party party = partyRepository.findByChatRoomId(chatRoomId)
                .orElseThrow(() -> new ResourceNotFoundException(PARTY_NOT_FOUND));
        return ChatRoomDetailGetResponse.of(party, chatRoomUserRepository.countChatRoomUsersByChatRoomId(chatRoomId));
    }

    // [CHECK] 1. slice or page or list 2. PageResponse api
    public ChatMessagePageResponse getChatMessage(Long chatRoomId, PageGetRequest pageGetRequest) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("ChatRoom not found"));
        log.info("chatRoom.id = {}", chatRoom.getId());
        PageRequest pageRequest = PageRequest.of(pageGetRequest.page(), pageGetRequest.size(), Sort.by(Sort.Direction.DESC, "id"));
        log.info("pageGetRequest.page() = {}", pageGetRequest.page());
//        List<ChatMessageGetResponse> chatMessages = chatMessageRepository.findAllByChatRoom(chatRoom, pageRequest)
//                .stream().map(ChatMessageGetResponse::from).toList();
        Slice<ChatMessage> chatMessagesSlice = chatMessageRepository.findAllByChatRoomId(chatRoom.getId(), pageRequest);
        log.info("chatMessagesSlice = {}", chatMessagesSlice);

        return ChatMessagePageResponse.from(chatMessagesSlice);
    }

    // [CHECK]
    public PageResponse<List<ChatRoomsGetResponse>> getChatRooms() {
        return null;
    }
}
