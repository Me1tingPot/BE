package meltingpot.server.domain.entity.chat;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "socket_session")
public class SocketSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "session_id")
    private String sessionId;

    @NotNull
    @Column(name = "username")
    private String username;

    @Column(name = "chatRoom_id")
    private Long chatRoomId;

    public void setChatRoomId(Long chatRoomId) {
        this.chatRoomId = chatRoomId;
    }
}
