package meltingpot.server.user.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import meltingpot.server.user.service.dto.UpdateNameServiceDto;
import meltingpot.server.util.VerificationUtil;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateNameRequestDto {

    @NotBlank(message = "nickname is required")
    @Pattern(regexp = VerificationUtil.NAME_REGEXP)
    private String nickname;

    public UpdateNameServiceDto toServiceDto() {
        return UpdateNameServiceDto.builder()
                .name(getNickname())
                .build();
    }


}
