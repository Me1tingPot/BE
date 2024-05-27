package meltingpot.server.party.dto;

import lombok.Builder;

@Builder
public record PartyReportRequest(
    String reportContent
) {
}
