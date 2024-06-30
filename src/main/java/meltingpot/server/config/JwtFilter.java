package meltingpot.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import meltingpot.server.util.ResponseCode;
import meltingpot.server.util.ResponseData;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REFRESH_TOKEN_HEADER = "RefreshToken";
    public static final String BEARER_PREFIX = "Bearer";

    public static final Integer REFRESH_TOKEN_TYPE = 1;
    public static final Integer ACCESS_TOKEN_TYPE = 0;

    private final TokenProvider tokenProvider;
    private final ObjectMapper objectMapper;

    // 실제 필터링 로직은 doFilterInternal 에 들어감
    // JWT 토큰의 인증 정보를 현재 쓰레드의 SecurityContext 에 저장하는 역할 수행
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain)
            throws IOException, ServletException {

        // 1. Request Header 에서 토큰을 꺼냄
        String accessToken = resolveToken(request, ACCESS_TOKEN_TYPE);

        // 2. validateToken 으로 토큰 유효성 검사
        // 정상 토큰이면 해당 토큰으로 Authentication 을 가져와서 SecurityContext 에 저장
        // 권한이 필요하지 않은 요청은 custom jwt filter를 거치지 않도록 설정
        if (request.getRequestURI().contains("/contract") || request.getRequestURI()
                .contains("/api/v1/auth") || request.getRequestURI().contains("/api/v1/mail")
                || request.getRequestURI().contains("/docs") || request.getRequestURI()
                .contains("/favicon.ico") || request.getRequestURI().contains("/h2-console") ||
                request.getRequestURI().contains("/swagger-ui") ||
                request.getRequestURI().contains("/api-docs") ||
                request.getRequestURI().equals("/chat")) {
            filterChain.doFilter(request, response);
        }
        // 권한이 필요한 요청은 custom jwt filter를 거치도록 설정
        else {
            //token 인증 성공 시
            if (StringUtils.hasText(accessToken) && tokenProvider.validateToken(accessToken)) {
                Authentication authentication = tokenProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
            }
            //token 인증 실패 시
            else {
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(objectMapper
                        .writeValueAsString(ResponseData.of(ResponseCode.INVALID_AUTH_TOKEN)));
                response.setContentType("application/json");
            }
        }
    }

    // Request Header 에서 토큰 정보를 꺼내오기
    private String resolveToken(HttpServletRequest request, Integer tokenType) {
        String bearerToken;

        if (tokenType.equals(ACCESS_TOKEN_TYPE)) {
            bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        } else {
            bearerToken = request.getHeader(REFRESH_TOKEN_HEADER);
        }

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }
}