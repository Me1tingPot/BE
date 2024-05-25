package meltingpot.server.chat.service;

import lombok.RequiredArgsConstructor;
import meltingpot.server.chat.dto.ChatRoomDetailGetResponse;
import meltingpot.server.domain.entity.party.Party;
import meltingpot.server.domain.repository.chat.ChatMessageRepository;
import meltingpot.server.domain.repository.chat.ChatRoomRepository;
import meltingpot.server.domain.repository.chat.ChatRoomUserRepository;
import meltingpot.server.domain.repository.party.PartyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomQueryService {
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final PartyRepository partyRepository;

    public ChatRoomDetailGetResponse getRoomDetail(Long chatRoomId) {
        // imageKey, title, userCnt
        Party party = partyRepository.findByChatRoomId(chatRoomId);
        return ChatRoomDetailGetResponse.of(party, chatRoomUserRepository.countChatRoomUsersByChatRoomId(chatRoomId));
    }
}
