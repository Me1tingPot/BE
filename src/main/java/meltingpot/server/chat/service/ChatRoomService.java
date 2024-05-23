package meltingpot.server.chat.service;

import lombok.RequiredArgsConstructor;
import meltingpot.server.domain.repository.ChatRoomUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomUserRepository chatRoomUserRepository;

    public void updateAlarmStatus() {
    }
}
