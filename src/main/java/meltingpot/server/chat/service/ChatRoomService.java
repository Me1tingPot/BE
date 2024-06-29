package meltingpot.server.chat.service;

import lombok.RequiredArgsConstructor;
import meltingpot.server.chat.dto.ChatRoomAlarmUpdateRequest;
import meltingpot.server.domain.entity.chat.ChatRoomUser;
import meltingpot.server.domain.repository.chat.ChatRoomUserRepository;
import meltingpot.server.exception.ResourceNotFoundException;
import meltingpot.server.util.ResponseCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static meltingpot.server.util.ResponseCode.CHAT_ROOM_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomUserRepository chatRoomUserRepository;

    public ResponseCode updateAlarmStatus(Long userId, ChatRoomAlarmUpdateRequest request) {
        ChatRoomUser chatRoomUser = chatRoomUserRepository.findChatRoomUserByUserIdAndChatRoomId(userId, request.chatRoomId())
                .orElseThrow(() -> new ResourceNotFoundException(CHAT_ROOM_NOT_FOUND));
        chatRoomUser.toggleAlarm();
        chatRoomUserRepository.save(chatRoomUser);
        return ResponseCode.CHAT_ALARM_UPDATE_SUCCESS;
    }
}
