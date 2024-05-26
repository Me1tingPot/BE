package meltingpot.server.party.dto;

import lombok.Builder;
import meltingpot.server.domain.entity.party.PartyContent;

@Builder
public record PartyContentResponse(
    String lang,
    String content
) {
    public static PartyContentResponse of(PartyContent partyContent) {
        return PartyContentResponse.builder()
            .lang(partyContent.getPartyContentLang())
            .content(partyContent.getPartyContent())
            .build();
    }
}
