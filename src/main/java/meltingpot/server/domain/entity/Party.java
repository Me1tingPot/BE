package meltingpot.server.domain.entity;

import jakarta.persistence.*;
import lombok.*;

// chat 파일 오류 방지 위해서 party 파일 임시로 작성한거라서 무시하셔도 됩니다!
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class Party {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "party_id")
    private Long id;
}
