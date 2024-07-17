package meltingpot.server.auth.controller.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReissueTokenResponseDto {

    private final Long accountId;
    private final String accessToken;
    private final String refreshToken;

}