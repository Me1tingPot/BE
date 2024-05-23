package meltingpot.server.domain.repository;

import meltingpot.server.domain.entity.chat.ChatRoomUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Long> {
}
