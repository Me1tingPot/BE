package meltingpot.server.domain.repository.chat;

import meltingpot.server.domain.entity.chat.ChatRoom;
import meltingpot.server.domain.entity.chat.ChatRoomUser;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Long> {
    Optional<ChatRoomUser> findChatRoomUserByUserIdAndChatRoomId(Long userId, Long chatRoomId);
    Slice<ChatRoomUser> findAllByUserId(Long userId, PageRequest pageRequest);
    List<ChatRoomUser> findAllByChatRoom(ChatRoom chatRoom);
    List<ChatRoomUser> findAllByUserId(Long userId);
}
