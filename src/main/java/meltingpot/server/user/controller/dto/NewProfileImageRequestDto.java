package meltingpot.server.user.controller.dto;

import lombok.Builder;

@Builder
public record NewProfileImageRequestDto(
        String imageKey,
        int sequence
) {
}
