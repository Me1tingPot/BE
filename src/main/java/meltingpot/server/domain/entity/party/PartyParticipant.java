package meltingpot.server.domain.entity.party;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.common.BaseEntity;
import meltingpot.server.domain.entity.party.enums.ParticipantStatus;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PartyParticipant extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id")
    private Party party;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private Account account;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ParticipantStatus participantStatus;
}
