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
    Integer areaId,
    Integer partyMinParticipant,
    Integer partyMaxParticipant,
    Boolean locationIsReserved,
    Boolean locationCanBeChanged,
    Boolean isTempSave
) {
}
