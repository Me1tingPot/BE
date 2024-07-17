package meltingpot.server.user.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import meltingpot.server.util.VerificationUtil;

public record UpdateNameRequestDto(
        @NotBlank(message = "nickname is required")
        @Pattern(regexp = VerificationUtil.NAME_REGEXP)
        String nickname
) {
}
