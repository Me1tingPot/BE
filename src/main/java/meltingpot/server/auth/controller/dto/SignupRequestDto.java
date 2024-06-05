package meltingpot.server.auth.controller.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record SignupRequestDto(
        String username,
        String password,
        String name,
        String gender,
        LocalDate birth,
        String nationality,
        List<String> languages,
        List<ProfileImageSignupDto> profileImages

){}