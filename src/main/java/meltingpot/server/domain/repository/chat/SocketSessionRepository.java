package meltingpot.server.domain.repository.chat;

import meltingpot.server.domain.entity.chat.SocketSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SocketSessionRepository extends JpaRepository<SocketSession, String> {
    List<SocketSession> findByChatRoomId(Long chatRoomId);
    SocketSession findByUsernameAndChatRoomId(String userName, Long chatRoomId);
    SocketSession findBySessionId(String sessionId);
}
