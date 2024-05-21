package meltingpot.server.domain.entity;

import jakarta.persistence.*;
import lombok.*;


//연관관계 만들려고 제가 임시로 만든거라서 merge할때 무시하시면 됩니다,,!
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
}
