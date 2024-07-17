package meltingpot.server.auth.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import meltingpot.server.util.VerificationUtil;

public record VerificationCodeRequestDto(
        @NotBlank(message = "email is required")
        @Pattern(regexp = VerificationUtil.USERNAME_REGEXP)
        String email,
        @NotBlank
        String code
) {
}
