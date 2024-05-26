package meltingpot.server.party.dto;

import lombok.Builder;
import meltingpot.server.domain.entity.party.Party;
import meltingpot.server.domain.entity.party.enums.PartyStatus;

@Builder
public record PartyResponse(
    int id,
    String ownerName,
    String subject,
    PartyStatus partyStatus,
    String startTime,
    String locationAddress,
    String locationDetail,
    Boolean locationReserved,
    Boolean locationCanBeChanged,
    int minParticipant,
    int maxParticipant
) {
    public static PartyResponse of(Party party) {
        return PartyResponse.builder().id(party.getId())
            .ownerName(party.getAccount().getName())
            .subject(party.getPartySubject())
            .partyStatus(party.getPartyStatus())
            .startTime(party.getPartyStartTime().toString())
            .locationAddress(party.getPartyLocationAddress())
            .locationDetail(party.getPartyLocationDetail())
            .locationReserved(party.getPartyLocationReserved())
            .locationCanBeChanged(party.getPartyLocationCanBeChanged())
            .minParticipant(party.getPartyMinParticipant())
            .maxParticipant(party.getPartyMinParticipant())
            .build();
    }
}
