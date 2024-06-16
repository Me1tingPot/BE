package meltingpot.server.auth.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileImageRequestDto {
    String imageKey;
    boolean thumbnail;
    int sequence;
}
