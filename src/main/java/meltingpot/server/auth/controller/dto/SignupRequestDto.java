package meltingpot.server.auth.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import meltingpot.server.util.VerificationUtil;

import java.time.LocalDate;
import java.util.List;

@Builder
public record SignupRequestDto(
        @NotBlank(message = "email is required")
        @Pattern(regexp = VerificationUtil.USERNAME_REGEXP)
        String email,
        @NotBlank(message = "password is required")
        @Pattern(regexp = VerificationUtil.PASSWORD_REGEXP)
        String password,
        @NotBlank(message = "name is required")
        @Pattern(regexp = VerificationUtil.NAME_REGEXP)
        String name,
        String gender,
        LocalDate birth,
        String nationality,
        List<String> languages,
        List<ProfileImageSignupDto> profileImages

){}