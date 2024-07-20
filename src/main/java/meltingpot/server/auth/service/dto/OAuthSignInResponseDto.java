package meltingpot.server.auth.service.dto;

import lombok.Builder;

@Builder
public record OAuthSignInResponseDto(
        String accessToken,
        String refreshToken,
        String email,
        String nickName,
        boolean register_required
) {
}
