package meltingpot.server.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meltingpot.server.chat.dto.*;
import meltingpot.server.domain.entity.chat.ChatMessage;
import meltingpot.server.domain.entity.chat.ChatRoom;
import meltingpot.server.domain.entity.chat.ChatRoomUser;
import meltingpot.server.domain.repository.chat.ChatMessageRepository;
import meltingpot.server.domain.repository.chat.ChatRoomRepository;
import meltingpot.server.domain.repository.chat.ChatRoomUserRepository;
import meltingpot.server.domain.repository.party.PartyRepository;
import meltingpot.server.exception.ResourceNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static meltingpot.server.util.ResponseCode.CHAT_ROOM_NOT_FOUND;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomQueryService {
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final PartyRepository partyRepository;

    // [CHECK] 1. slice or page or list 2. PageResponse api
    public ChatMessagePageResponse getChatMessages(Long userId, Long chatRoomId, int page, int size) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ResourceNotFoundException(CHAT_ROOM_NOT_FOUND));

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Slice<ChatMessage> chatMessagesSlice = chatMessageRepository.findAllByChatRoomId(chatRoom.getId(), pageRequest);

        return ChatMessagePageResponse.from(chatMessagesSlice, chatRoom);
    }

    public ChatRoomsPageResponse getChatRooms(Long userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "exitAt"));

        Slice<ChatRoomUser> chatRoomUserSlice = chatRoomUserRepository.findAllByUserId(userId, pageRequest);

        return ChatRoomsPageResponse.from(chatRoomUserSlice);
    }
}
