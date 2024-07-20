package meltingpot.server.auth.controller.dto;

import meltingpot.server.domain.entity.enums.OAuthType;

public record OAuthSignInRequestDto(
        OAuthType type,
        String code,
        String push_token
) {
}
