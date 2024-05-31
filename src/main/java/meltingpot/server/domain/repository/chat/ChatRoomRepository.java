package meltingpot.server.domain.repository.chat;

import meltingpot.server.domain.entity.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}
