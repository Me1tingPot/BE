package meltingpot.server.user.controller.dto;

import lombok.Builder;
import lombok.Getter;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.AccountProfileImage;
import org.apache.catalina.User;

@Getter
@Builder
public class UserResponseDto {
    private Long id;
    private String email; // username
    private String bio;
    //private String thumbnail;

    public static UserResponseDto of(Account account ){
        return UserResponseDto.builder()
                .id(account.getId())
                .email(account.getUsername())
                .bio(account.getBio())
                //.thumbnail(thumbnail.getImageKey())
                .build();
    }

}
