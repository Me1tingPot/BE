package meltingpot.server.domain.entity.chat;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import meltingpot.server.domain.entity.chat.enums.Alarm;
import meltingpot.server.domain.entity.common.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "chat_room_user")
public class ChatRoomUser extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "user_id")
    private Long userId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "chat_room_alarm_status")
    private Alarm alarm;

    @NotNull
    @Column(name = "exit_at")
    private LocalDateTime exitAt;

    @NotNull
    @Column(name = "unread_message_cnt")
    private int unreadMessageCnt;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    public void toggleAlarm() {
        this.alarm = this.alarm.toggle();
    }
}
