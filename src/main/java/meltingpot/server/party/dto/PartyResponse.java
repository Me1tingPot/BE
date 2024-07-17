package meltingpot.server.party.dto;

import lombok.Builder;
import meltingpot.server.domain.entity.party.Party;
import meltingpot.server.domain.entity.party.enums.PartyStatus;

import java.util.List;

@Builder
public record PartyResponse(
    int id,
    PartyOwnerResponse owner,
    String subject,
    PartyStatus partyStatus,
    String startTime,
    String locationAddress,
    String locationDetail,
    Boolean locationReserved,
    Boolean locationCanBeChanged,
    int minParticipant,
    int maxParticipant,
    List<PartyParticipantResponse> participants,
    List<PartyContentResponse> contents
) {
    public static PartyResponse of(Party party) {
        return PartyResponse.builder().id(party.getId())
            .owner(PartyOwnerResponse.of(party.getAccount()))
            .subject(party.getPartySubject())
            .partyStatus(party.getPartyStatus())
            .startTime(party.getPartyStartTime().toString())
            .locationAddress(party.getPartyLocationAddress())
            .locationDetail(party.getPartyLocationDetail())
            .locationReserved(party.getPartyLocationReserved())
            .locationCanBeChanged(party.getPartyLocationCanBeChanged())
            .minParticipant(party.getPartyMinParticipant())
            .maxParticipant(party.getPartyMinParticipant())
            .participants(party.getPartyParticipants().stream().map(PartyParticipantResponse::of).toList())
            .contents(party.getPartyContents().stream().map(PartyContentResponse::of).toList())
            .build();
    }
}
