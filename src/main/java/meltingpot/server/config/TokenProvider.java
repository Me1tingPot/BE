package meltingpot.server.config;

import io.jsonwebtoken.*;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.RefreshToken;
import meltingpot.server.domain.repository.RefreshTokenRepository;
import meltingpot.server.domain.repository.AccountRepository;
import meltingpot.server.exception.InvalidTokenException;
import meltingpot.server.exception.ResourceNotFoundException;
import meltingpot.server.util.ResponseCode;
import meltingpot.server.util.TokenDto;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TokenProvider {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AccountRepository accountRepository;

    private static final String AUTHORITIES_KEY = "auth";

    private static final String UUID_KEY = "uuid";
    private static final String BEARER_TYPE = "bearer";

    private static final long ACCESS_TOKEN_EXPIRE_TIME = (long) 1000 * 60 * 30;            // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = (long) 1000 * 60 * 60 * 24 * 30;  // 30일

    private final Key key;

    public TokenProvider(@Value("${jwt.secret}") String secretKey,
                         RefreshTokenRepository refreshTokenRepository, AccountRepository accountRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.accountRepository = accountRepository;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenDto generateTokenDto(Authentication authentication){
        // 권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        String uuid = UUID.randomUUID().toString();

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(UUID_KEY, uuid)
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();

        return TokenDto.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .refreshToken(refreshToken)
                .build();
    }


    // 재발급 TokenDto 반환
    public TokenDto generateReissuedTokenDto(String accessToken) {
        // accessToken에서 username 추출
        String username = parseClaims(accessToken).getSubject();
        // username으로 account 조회
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseCode.ACCOUNT_NOT_FOUND));
        // account에서 account_roles -> authorities로 변환
        String authorities = account.toAuthStringList().stream().collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String uuid = UUID.randomUUID().toString();

        // Access Token 생성
        String newAccessToken = Jwts.builder()
                .setSubject(username)
                .claim(AUTHORITIES_KEY, authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();

        // Refresh Token 생성
        String newRefreshToken = Jwts.builder()
                .setSubject(username)
                .claim(UUID_KEY, uuid)
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();

        return TokenDto.builder()
                .grantType(BEARER_TYPE)
                .accessToken(newAccessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .refreshToken(newRefreshToken)
                .build();
    }

    public String generateSocketToken(Account account) {
        final long now = (new Date()).getTime();
        final long expiredTime = (long) 1000 * 60;
        Date accessTokenExpiresIn = new Date(now + expiredTime);

        String socketToken = Jwts.builder()
                .setSubject(account.getUsername())
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return socketToken;
    }

    public Claims getSocketTokenClaims(String accessToken) {
        return parseClaims(accessToken);
    }

    // 재발급한 RefreshToken 저장
    public void updateRefreshToken(String accessToken, String newRefreshToken) {
        String username = parseClaims(accessToken).getSubject();
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseCode.ACCOUNT_NOT_FOUND));

        // 재발급한 refresh token 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .account(account)
                .tokenValue(newRefreshToken)
                .build();
        refreshTokenRepository.save(refreshToken);
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    public Authentication getAuthentication(String accessToken) {
        // 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new InvalidTokenException(ResponseCode.INVALID_AUTH_TOKEN);
        }

        Collection<? extends GrantedAuthority> authorities;
        String[] rawAuthorities = claims.get(AUTHORITIES_KEY).toString().split(",");
        if (rawAuthorities.length == 1 && rawAuthorities[0].isEmpty()) {
            authorities = Collections.emptyList();
        } else {
            authorities = Arrays.stream(rawAuthorities)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }

        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, accessToken, authorities);
    }


    private Claims parseClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(key).parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
