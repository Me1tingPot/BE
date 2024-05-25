package meltingpot.server.domain.repository.chat;

import meltingpot.server.domain.entity.chat.ChatMessage;
import meltingpot.server.domain.entity.chat.ChatRoom;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Optional<ChatRoom> findByChatRoomChatRoomId(Long id);
    Slice<ChatMessage> findAllByChatRoom(ChatRoom chatRoomJpaEntity, PageRequest pageRequest);
}
