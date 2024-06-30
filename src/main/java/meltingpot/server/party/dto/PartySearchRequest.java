package meltingpot.server.party.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record PartySearchRequest(
    Integer page,
    String query,
    String areaIdFilter,
    List<String> temporalFilter,
    String statusFilter,
    PartyCoordRequest coordLeftTopFilter,
    PartyCoordRequest coordRightBottomFilter
) {
    public static PartySearchRequest of(
            Integer page,
            String query,
            String areaIdFilter,
            List<String> temporalFilter,
            String statusFilter,
            PartyCoordRequest coordLeftTopFilter,
            PartyCoordRequest coordRightBottomFilter
    ) {
        return new PartySearchRequest(
            page,
            query,
            areaIdFilter,
            temporalFilter,
            statusFilter,
            coordLeftTopFilter,
            coordRightBottomFilter
        );
    }
}
