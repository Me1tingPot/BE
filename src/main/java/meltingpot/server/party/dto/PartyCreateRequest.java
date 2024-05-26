package meltingpot.server.party.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record PartyCreateRequest(
    String subject,
    List<String> imageKey,
    String locationAddress,
    String locationDetail,
    String description,
    String descriptionLanguage,
    String startTime,
    int areaId,
    int partyMinParticipant,
    int partyMaxParticipant,
    Boolean locationIsReserved,
    Boolean locationCanBeChanged,
    Boolean isTempSave
) {
}
