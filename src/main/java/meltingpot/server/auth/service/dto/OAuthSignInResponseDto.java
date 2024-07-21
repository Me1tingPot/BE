package meltingpot.server.auth.service.dto;

import lombok.Builder;
import meltingpot.server.util.TokenDto;

@Builder
public record OAuthSignInResponseDto(
        boolean register_required,
        String email,
        String nickName,
        TokenDto tokenDto
) {
}
