package meltingpot.server.party.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record PartySearchRequest(
    Integer page,
    String query,
    Integer areaIdFilter,
    List<String> temporalFilter,
    String statusFilter
) {
    public static PartySearchRequest of(
            Integer page,
            String query,
            Integer areaIdFilter,
            List<String> temporalFilter,
            String statusFilter
    ) {
        return new PartySearchRequest(
            page,
            query,
            areaIdFilter,
            temporalFilter,
            statusFilter
        );
    }
}
