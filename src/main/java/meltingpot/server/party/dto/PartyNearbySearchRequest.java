package meltingpot.server.party.dto;

import lombok.Builder;

@Builder
public record PartyNearbySearchRequest(
    Integer page,
    String areaId
) {
    public static PartyNearbySearchRequest of(
            Integer page,
            String areaId
    ) {
        return new PartyNearbySearchRequest(
            page,
            areaId
        );
    }
}
