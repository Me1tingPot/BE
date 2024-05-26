package meltingpot.server.party.dto;

import lombok.Builder;
import meltingpot.server.domain.entity.party.PartyParticipant;

@Builder
public record PartyParticipantResponse(
    String name
) {
    public static PartyParticipantResponse of(PartyParticipant partyParticipant) {
        return PartyParticipantResponse.builder()
            .name(partyParticipant.getAccount().getName())
            .build();
    }
}
