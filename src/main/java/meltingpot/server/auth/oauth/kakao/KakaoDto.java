package meltingpot.server.auth.oauth.kakao;

import lombok.Builder;
import lombok.Data;
@Builder
@Data
public class KakaoDto {
    private String email;
    private String nickname;

}