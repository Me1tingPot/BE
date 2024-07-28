package meltingpot.server.auth.controller.dto;

import meltingpot.server.domain.entity.enums.OAuthType;

public record OAuthSignInRequestDto(
        OAuthType type,
        String token,
        String push_token
) {
}
