package meltingpot.server.domain.entity.chat;

import jakarta.persistence.*;
import lombok.*;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.chat.enums.Alarm;
import meltingpot.server.domain.entity.common.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "chat_room_user")
public class ChatRoomUser extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_user_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "chat_room_alarm_status")
    private Alarm alarm;

    @Column(name = "account_id")
    private Long accountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    public void toggleAlarm() {
        this.alarm = this.alarm.toggle();
    }
}
