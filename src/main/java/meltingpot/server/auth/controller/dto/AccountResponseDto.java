package meltingpot.server.auth.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.util.TokenDto;

@Getter
@Setter
@Builder
public class AccountResponseDto {
    private final Long id;
    private final String email;
    private final String name;
    private TokenDto tokenDto;

    public static AccountResponseDto of(Account account) {
        return AccountResponseDto.builder()
                .id(account.getId())
                .email(account.getUsername())
                .name(account.getName())
                .build();
    }


}
