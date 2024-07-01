package meltingpot.server.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meltingpot.server.chat.dto.*;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.AccountProfileImage;
import meltingpot.server.domain.entity.chat.ChatMessage;
import meltingpot.server.domain.entity.chat.ChatRoom;
import meltingpot.server.domain.entity.chat.ChatRoomUser;
import meltingpot.server.domain.repository.AccountProfileImageRepository;
import meltingpot.server.domain.repository.chat.ChatMessageRepository;
import meltingpot.server.domain.repository.chat.ChatRoomRepository;
import meltingpot.server.domain.repository.chat.ChatRoomUserRepository;
import meltingpot.server.domain.repository.party.PartyRepository;
import meltingpot.server.exception.ResourceNotFoundException;
import meltingpot.server.util.r2.FileService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    private final AccountProfileImageRepository accountProfileImageRepository;
    private final FileService fileService;

    public ChatMessagePageResponse getChatMessages(Account account, Long chatRoomId, int page, int size) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ResourceNotFoundException(CHAT_ROOM_NOT_FOUND));

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Slice<ChatMessage> chatMessagesSlice = chatMessageRepository.findAllByChatRoomId(chatRoom.getId(), pageRequest);

        List<ChatMessageGetResponse> chatMessageGetResponseList = chatMessagesSlice.stream()
                .map(chatMessage -> ChatMessageGetResponse.from(chatMessage, getThumbnailUrl(chatMessage.getAccount())))
                .toList();

        String leaderThumbnailUrl = getThumbnailUrl(chatRoom.getParty().getAccount());

        return ChatMessagePageResponse.from(chatMessageGetResponseList, chatRoom, leaderThumbnailUrl, chatMessagesSlice);
    }

    public ChatRoomsPageResponse getChatRooms(Account account, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "exitAt"));

        Slice<ChatRoomUser> chatRoomUsersSlice = chatRoomUserRepository.findAllByUserId(account.getId(), pageRequest);

        List<ChatRoomGetResponse> chatRoomGetResponseList = chatRoomUsersSlice.stream()
                .map(chatRoomUser -> ChatRoomGetResponse.from(chatRoomUser, getThumbnailUrl(account)))
                .toList();

        return ChatRoomsPageResponse.from(chatRoomGetResponseList, chatRoomUsersSlice);
    }

    public String getThumbnailUrl(Account account) {
        AccountProfileImage thumbnail = accountProfileImageRepository.findByAccountAndIsThumbnailTrue(account).orElseThrow();

        return fileService.getCdnUrl("userProfile-image",  thumbnail.getImageKey());
    }
}
