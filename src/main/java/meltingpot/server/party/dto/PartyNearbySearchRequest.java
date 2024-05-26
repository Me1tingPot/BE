package meltingpot.server.party.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record PartyNearbySearchRequest(
    Integer page,
    Integer areaId
) {
    public static PartyNearbySearchRequest of(
            Integer page,
            Integer areaId
    ) {
        return new PartyNearbySearchRequest(
            page,
            areaId
        );
    }
}
