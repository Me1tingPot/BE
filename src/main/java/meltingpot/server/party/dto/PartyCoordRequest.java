package meltingpot.server.party.dto;

import lombok.Builder;

@Builder
public record PartyCoordRequest(
    double latitude,
    double longitude
) {
}
