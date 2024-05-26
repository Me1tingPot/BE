package meltingpot.server.domain.entity.party;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import meltingpot.server.domain.entity.Area;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.common.BaseEntity;
import meltingpot.server.domain.entity.party.enums.PartyStatus;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Party extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "party_owner")
    private Account account;

    @NotNull
    private String partySubject;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PartyStatus partyStatus;

    @NotNull
    private LocalDateTime partyStartTime;

    private LocalDateTime partyEndTime;

    @NotNull
    private String partyLocationAddress;

    @NotNull
    private String partyLocationDetail;

    @NotNull
    private Boolean partyLocationReserved;

    @NotNull
    private Boolean partyLocationCanBeChanged;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_area")
    private Area partyArea;

    @NotNull
    private int partyMinParticipant;

    @NotNull
    private int partyMaxParticipant;

    @NotNull
    @Column(name = "part_max_participant", nullable = false)
    private Integer partMaxParticipant;
}
