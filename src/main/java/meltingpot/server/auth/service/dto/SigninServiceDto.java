package meltingpot.server.auth.service.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter
@Builder
public class SigninServiceDto {

    private final String username;
    private final String password;

    // 미인증 토큰 생성
    public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(getUsername(), getPassword());
    }
}
