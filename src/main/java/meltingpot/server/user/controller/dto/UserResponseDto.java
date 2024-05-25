package meltingpot.server.user.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import meltingpot.server.domain.entity.User;
import meltingpot.server.util.TokenDto;

@Getter
@Setter
@Builder
public class UserResponseDto {
    private final Long id;
    private final String email;
    private final String username;
    private TokenDto tokenDto;

    public static UserResponseDto of(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .build();
    }


}
