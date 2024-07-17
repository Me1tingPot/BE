package meltingpot.server.user.controller.dto;

import jakarta.validation.constraints.Pattern;
import meltingpot.server.util.VerificationUtil;

public record UpdateBioRequestDto(
        @Pattern(regexp = VerificationUtil.BIO_REGEXP)
        String bio
) {
}