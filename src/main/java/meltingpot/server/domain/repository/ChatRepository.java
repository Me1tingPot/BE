package meltingpot.server.domain.repository;

import meltingpot.server.domain.entity.chat.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {
}
