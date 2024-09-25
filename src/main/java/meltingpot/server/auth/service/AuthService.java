package meltingpot.server.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meltingpot.server.auth.controller.dto.*;
import meltingpot.server.domain.entity.*;
import meltingpot.server.domain.entity.enums.Gender;
import meltingpot.server.domain.entity.enums.OAuthType;
import meltingpot.server.domain.repository.AccountPushTokenRepository;
import meltingpot.server.domain.repository.MailVerificationRepository;
import meltingpot.server.exception.*;
import meltingpot.server.config.TokenProvider;
import meltingpot.server.domain.repository.RefreshTokenRepository;
import meltingpot.server.domain.repository.AccountRepository;
import meltingpot.server.auth.service.dto.SigninServiceDto;
import meltingpot.server.exception.IllegalArgumentException;
import meltingpot.server.util.AccountUser;
import meltingpot.server.util.ResponseCode;
import meltingpot.server.util.SecurityUtil;
import meltingpot.server.util.TokenDto;
import meltingpot.server.util.r2.FileService;
import meltingpot.server.util.r2.FileUploadResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
@EnableWebSecurity
public class AuthService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;
    private final AccountPushTokenRepository accountPushTokenRepository;
    private final MailVerificationRepository mailVerificationRepository;

    // 회원가입
    @Transactional
    public AccountResponseDto signup(SignupRequestDto signupRequest) {

        // 이메일 인증을 거친 유효한 이메일인지 확인
        if(!mailVerificationRepository.existsByEmailAndVerifiedTrue(signupRequest.email())){
            throw new AuthException(ResponseCode.MAIL_NOT_AUTHORIZED);
        };

        // 이미 가입한 이메일인지 확인
        if(accountRepository.existsByUsername(signupRequest.email())){
            throw new AuthException(ResponseCode.EMAIL_DUPLICATION);
        }

        // 프로필 사진 개수 확인
        if(signupRequest.profileImages().isEmpty()){
            throw new AuthException(ResponseCode.PROFILE_IMAGE_LESS_THAN_ONE);
        }
        if(signupRequest.profileImages().size()>4){
            throw new AuthException(ResponseCode.PROFILE_IMAGE_MORE_THAN_FOUR);
        }

        // 프로필 이미지 썸네일 지정 여부 확인
        boolean thumbnail_check = false;
        for(ProfileImageRequestDto image : signupRequest.profileImages()){
            if (image.isThumbnail()) {
                if(thumbnail_check) throw new IllegalArgumentException(ResponseCode.THUMBNAIL_IS_DUPLICATED);
                thumbnail_check = true;
            }
        }
        if(!thumbnail_check) throw new AuthException(ResponseCode.THUMBNAIL_NOT_FOUND);


        // 시퀀스 모두 다른지 확인
        Set<Integer> sequences = new HashSet<>();
        for (ProfileImageRequestDto profileImage : signupRequest.profileImages()) {
            if (!sequences.add(profileImage.getSequence())) {
                throw new IllegalArgumentException(ResponseCode.PROFILE_IMAGE_SEQUENCE_IS_DUPLICATED);
            }
        }

        // 성별 유효성 확인
        boolean gender_check = false;
        for( Gender gender : Gender.values()){
            if(gender.toString().equals(signupRequest.gender())) gender_check = true;
        }
        if(!gender_check) throw new IllegalArgumentException(ResponseCode.INVALID_GENDER_IS_PROVIDED);


        Account account = Account.builder()
                .username(signupRequest.email())
                .name(signupRequest.name())
                .password(passwordEncoder.encode(signupRequest.password()))
                .gender(Gender.valueOf(signupRequest.gender()))
                .birth(signupRequest.birth())
                .nationality(signupRequest.nationality())
                .isQuit(false)
                .OAuthType(OAuthType.NONE)
                .build();

        account.setProfileImages(signupRequest.profileImages().stream().map(
                (image) -> AccountProfileImage.builder()
                        .account(account)
                        .imageKey(image.getImageKey())
                        .isThumbnail(image.isThumbnail())
                        .sequence(image.getSequence())
                        .imageOriginalName("")
                        .build()).toList()
        );

        if(signupRequest.languages()!=null){
            account.setLanguages(signupRequest.languages().stream().map(
                    (language) -> AccountLanguage.builder()
                            .account(account)
                            .language(language)
                            .build()).toList());
        }

        accountRepository.save(account);

        return signin(SigninServiceDto.builder()
                .username(account.getUsername())
                .password(signupRequest.password())
                .pushToken(signupRequest.pushToken())
                .build());
    }


    // 로그인
    @Transactional(rollbackFor = Exception.class)
    public AccountResponseDto signin(SigninServiceDto serviceDto){

        // 1. Login ID/PW 를 기반으로 AuthenticationToken 생성 (미인증 토큰)
        UsernamePasswordAuthenticationToken authenticationToken = serviceDto.toAuthentication();

        // 2. 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
        //    authenticate 메서드가 실행이 될 때 loadUserByUsername 메서드가 실행됨
        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        // 4. RefreshToken 저장
        Account account = accountRepository.findByUsernameAndIsQuitIsFalse(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException(ResponseCode.ACCOUNT_NOT_FOUND));
        RefreshToken refreshToken = RefreshToken.builder()
                .account(account)
                .tokenValue(tokenDto.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);

        if (!accountPushTokenRepository.existsAccountPushByAccountAndToken(account, serviceDto.getPushToken())) {
            AccountPushToken accountPushToken = AccountPushToken.builder()
                    .account(account)
                    .token(serviceDto.getPushToken())
                    .build();

            accountPushTokenRepository.save(accountPushToken);
        }

        //인증된 Authentication를 SecurityContext에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 5. 토큰 포함 현재 유저 정보 반환
        AccountResponseDto accountResponseDto = AccountResponseDto.of(getUserInfo());
        accountResponseDto.setTokenDto(tokenDto);

        return accountResponseDto;

    }

    // 로그아웃
    @Transactional(rollbackFor = Exception.class)
    public void signout(String refreshToken) {
        // refresh token 삭제
        if (Boolean.FALSE.equals(refreshTokenRepository.existsByTokenValue(refreshToken))) {
            throw new ResourceNotFoundException(ResponseCode.REFRESH_TOKEN_NOT_FOUND);
        }
        refreshTokenRepository.deleteByTokenValue(refreshToken);
    }


    // 사용자 이미지 업로드용 presignedUrl 생성
    @Transactional
    public FileUploadResponse generateImageUploadUrl() {
        return fileService.getPreSignedUrl("userProfile-image");
    }

    // 로그인 유저 정보 반환 to @CurrentUser
    @Transactional(readOnly = true)
    public Account getUserInfo(){
        return accountRepository.findByUsernameAndIsQuitIsFalse(SecurityUtil.getCurrentUserName())
                .orElseThrow(() -> new ResourceNotFoundException(ResponseCode.ACCOUNT_NOT_FOUND));
    }

    // 로그인시 유저정보 조회하는 메서드 override
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsernameAndIsQuitIsFalse(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return new AccountUser(account);
    }

    @Transactional(rollbackFor = Exception.class)
    public ReissueTokenResponseDto reissueToken( String oldAccessToken, String refreshToken ) {
        TokenDto reissuedTokenDto;

        // Bearer 접두사 제거 및 공백 제거
        oldAccessToken = oldAccessToken.replace("Bearer ", "").trim();
        refreshToken = refreshToken.trim();


        if (tokenProvider.validateToken(refreshToken) && Boolean.TRUE.equals(
                tokenProvider.validRefreshToken(refreshToken, oldAccessToken))) {
            reissuedTokenDto = tokenProvider.generateReissuedTokenDto(oldAccessToken);

            // 이전 refresh token 삭제
            RefreshToken targetRefreshToken = refreshTokenRepository.getByTokenValue(refreshToken);
            Long accountId = targetRefreshToken.getAccount().getId();
            refreshTokenRepository.deleteByTokenValue(refreshToken);

            tokenProvider.updateRefreshToken(oldAccessToken, reissuedTokenDto.getRefreshToken());

            return ReissueTokenResponseDto.builder()
                    .accountId(accountId)
                    .accessToken(reissuedTokenDto.getAccessToken())
                    .refreshToken(reissuedTokenDto.getRefreshToken())
                    .build();
        } else {
            throw new InvalidTokenException(ResponseCode.INVALID_REFRESH_TOKEN);
        }
    }
}
