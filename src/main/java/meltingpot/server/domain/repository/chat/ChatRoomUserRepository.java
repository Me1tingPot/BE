package meltingpot.server.domain.repository.chat;

import meltingpot.server.domain.entity.chat.ChatRoomUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Long> {
    int countChatRoomUsersByChatRoomId(Long chatRoomId);
    Optional<ChatRoomUser> findChatRoomUserByUserIdAndChatRoomId(Long userId, Long chatRoomId);
}
