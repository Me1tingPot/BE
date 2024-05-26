package meltingpot.server.chat.service;

import lombok.RequiredArgsConstructor;
import meltingpot.server.domain.entity.chat.ChatRoomUser;
import meltingpot.server.domain.repository.chat.ChatRoomUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomUserRepository chatRoomUserRepository;

    public void updateAlarmStatus(Long userId, Long chatRoomId) {
        ChatRoomUser chatRoomUser = chatRoomUserRepository.findChatRoomUserByUserIdAndChatRoomId(userId, chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("ChatRoomUser not found"));
        chatRoomUser.toggleAlarm();
        chatRoomUserRepository.save(chatRoomUser);
    }
}
