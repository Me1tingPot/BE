package meltingpot.server.auth.controller.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import meltingpot.server.auth.service.dto.SigninServiceDto;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SigninRequestDto {

    @NotBlank(message = "email is required")
    private String email;

    @NotBlank(message = "password is required")
    private String password;

    public SigninServiceDto toServiceDto() {
        return SigninServiceDto.builder()
                .username(getEmail())
                .password(getPassword())
                .build();
    }
}