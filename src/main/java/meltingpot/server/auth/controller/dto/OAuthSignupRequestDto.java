package meltingpot.server.auth.controller.dto;

import lombok.Builder;
import meltingpot.server.domain.entity.enums.OAuthType;

import java.time.LocalDate;
import java.util.List;

@Builder
public record OAuthSignupRequestDto(
        OAuthType OauthType,
        String email,
        String name,
        String gender,
        LocalDate birth,
        String nationality,
        List<String> languages,
        List<ProfileImageRequestDto> profileImages,
        String pushToken
) {
}
