package meltingpot.server.chat.service;

import lombok.RequiredArgsConstructor;
import meltingpot.server.chat.dto.ChatRoomAlarmUpdateRequest;
import meltingpot.server.domain.entity.chat.ChatMessage;
import meltingpot.server.domain.entity.chat.ChatRoomUser;
import meltingpot.server.domain.repository.chat.ChatMessageRepository;
import meltingpot.server.domain.repository.chat.ChatRoomUserRepository;
import meltingpot.server.exception.ResourceNotFoundException;
import meltingpot.server.util.ResponseCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public ResponseCode deleteChatRoomUser(Long userId, Long chatRoomId) {
        ChatRoomUser chatRoomUser = chatRoomUserRepository.findChatRoomUserByUserIdAndChatRoomId(userId, chatRoomId)
                .orElseThrow(() -> new ResourceNotFoundException(CHAT_ROOM_NOT_FOUND));
        chatRoomUserRepository.deleteById(chatRoomUser.getId());

        return ResponseCode.CHAT_ROOM_USER_DELETE_SUCCESS;
    }
}
