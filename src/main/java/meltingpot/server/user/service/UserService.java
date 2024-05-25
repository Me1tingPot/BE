package meltingpot.server.user.service;

import lombok.RequiredArgsConstructor;
import meltingpot.server.Exception.ResourceNotFoundException;
import meltingpot.server.config.TokenProvider;
import meltingpot.server.domain.RefreshToken;
import meltingpot.server.domain.entity.User;
import meltingpot.server.domain.repository.RefreshTokenRepository;
import meltingpot.server.domain.repository.UserRepository;
import meltingpot.server.user.controller.dto.UserResponseDto;
import meltingpot.server.user.service.dto.SigninServiceDto;
import meltingpot.server.util.ResponseCode;
import meltingpot.server.util.SecurityUtil;
import meltingpot.server.util.TokenDto;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    //private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    // 로그인 유저 정보 반환 to @CurrentUser
    @Transactional(readOnly = true)
    public User getUserInfo(){
        return userRepository.findByUsernameAndDeletedIsNull(SecurityUtil.getCurrentUserName())
                .orElseThrow(() -> new ResourceNotFoundException(ResponseCode.ACCOUNT_NOT_FOUND));
    }

    /*
    // 로그인시 유저 정보 조회하는 메서드 override
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        User user = userRepository.findByUsername(username)
                .orElseThrow()
    }
    
     */


    // 로그인
    @Transactional(rollbackFor = Exception.class)
    public UserResponseDto signin(SigninServiceDto serviceDto){

        // 1. Login ID/PW 를 기반으로 AuthenticationToken 생성 (미인증 토큰)
        UsernamePasswordAuthenticationToken authenticationToken = serviceDto.toAuthentication();

        // 2. 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
        //    authenticate 메서드가 실행이 될 때 loadUserByUsername 메서드가 실행됨
        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        // 4. RefreshToken 저장
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException(ResponseCode.ACCOUNT_NOT_FOUND));
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenValue(tokenDto.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);

        //인증된 Authentication를 SecurityContext에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 5. 토큰 포함 현재 유저 정보 반환
        UserResponseDto userResponseDto = UserResponseDto.of(getUserInfo());
        userResponseDto.setTokenDto(tokenDto);
        return userResponseDto;

    }

}
