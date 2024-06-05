package meltingpot.server.user.controller.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import meltingpot.server.user.service.dto.UpdateBioServiceDto;
import meltingpot.server.util.VerificationUtil;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBioRequestDto {

    @Pattern(regexp = VerificationUtil.BIO_REGEXP)
    private String bio;

    public UpdateBioServiceDto toServiceDto() {
        return UpdateBioServiceDto.builder()
                .bio(getBio())
                .build();
    }

}