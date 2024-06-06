package meltingpot.server.user.controller.dto;

import lombok.Builder;
import lombok.Getter;
import meltingpot.server.auth.controller.dto.ProfileImageSignupDto;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.AccountProfileImage;
import org.apache.catalina.User;

@Getter
@Builder
public class UserResponseDto {
    private Long id;
    private String email; // username
    private String name; // name
    private String bio;
    private String nationality; // 국적
    private int participate_count; // 참여 횟수
    private int host_count; // 주최 횟수
    private String thumbnail;

    public static UserResponseDto of(Account account, String thumbnailUrl, int host_count, int participate_count){
        return UserResponseDto.builder()
                .id(account.getId())
                .email(account.getUsername())
                .name(account.getName())
                .bio(account.getBio())
                .nationality(account.getNationality())
                .thumbnail(thumbnailUrl)
                .host_count(host_count)
                .participate_count(participate_count)
                .build();
    }

}
