package meltingpot.server.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import meltingpot.server.exception.UnknownAuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        // 유효한 자격증명을 제공하지 않고 접근하려 할때 401
        if (authException instanceof BadCredentialsException
                || authException instanceof InternalAuthenticationServiceException) {
            throw new BadCredentialsException("이메일이나 비밀번호가 맞지 않습니다");
        } else if (authException instanceof DisabledException) {
            throw new DisabledException("계정이 비활성화 되었습니다");
        } else if (authException instanceof CredentialsExpiredException) {
            throw new CredentialsExpiredException("비밀번호 유효기간이 만료되었습니다");
        } else {
            throw new UnknownAuthenticationException("알 수 없는 이유로 로그인에 실패했습니다");
        }
    }
}
