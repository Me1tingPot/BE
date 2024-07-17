package meltingpot.server.domain.repository.chat;

import meltingpot.server.domain.entity.chat.ChatMessage;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Slice<ChatMessage> findAllByChatRoomId(Long chatRoomId, PageRequest pageRequest);
}
