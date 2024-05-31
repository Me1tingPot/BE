package meltingpot.server.chat.dto;

import lombok.Builder;

@Builder
public record PageGetRequest(
        int page,
        int size
) {
}
