package meltingpot.server.auth.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record ReissueTokenRequestDto(
        @NotBlank
        String accessToken,
        @NotBlank
        String refreshToken
) {
}