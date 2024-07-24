package meltingpot.server.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meltingpot.server.auth.controller.dto.OAuthSignInRequestDto;
import meltingpot.server.auth.controller.dto.OAuthSignupRequestDto;
import meltingpot.server.auth.controller.dto.ProfileImageRequestDto;
import meltingpot.server.auth.oauth.OAuthUserDetails;
import meltingpot.server.auth.oauth.kakao.KaKaoTokenDto;
import meltingpot.server.auth.oauth.kakao.KakaoDto;
import meltingpot.server.auth.oauth.kakao.KakaoService;
import meltingpot.server.auth.service.dto.OAuthSignInResponseDto;
import meltingpot.server.config.TokenProvider;
import meltingpot.server.domain.entity.*;
import meltingpot.server.domain.entity.enums.Gender;
import meltingpot.server.domain.entity.enums.OAuthType;
import meltingpot.server.domain.repository.AccountPushTokenRepository;
import meltingpot.server.domain.repository.AccountRepository;
import meltingpot.server.domain.repository.RefreshTokenRepository;
import meltingpot.server.exception.AuthException;
import meltingpot.server.exception.IllegalArgumentException;
import meltingpot.server.util.ResponseCode;
import meltingpot.server.util.TokenDto;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
@EnableWebSecurity
public class OAuthService {
    private final AccountRepository accountRepository;
    private final KakaoService kakaoService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AccountPushTokenRepository accountPushTokenRepository;


    // SNS 회원 가입
    @Transactional
    public OAuthSignInResponseDto oauthSignup(OAuthSignupRequestDto signupRequest) {

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
                .password("")
                .gender(Gender.valueOf(signupRequest.gender()))
                .birth(signupRequest.birth())
                .nationality(signupRequest.nationality())
                .isQuit(false)
                .oAuthType(signupRequest.OauthType())
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

        account.setLanguages(signupRequest.languages().stream().map(
                (language) -> AccountLanguage.builder()
                        .account(account)
                        .language(language)
                        .build()).toList()
        );

        accountRepository.save(account);


        return OAuthSignInResponseDto.builder().
                register_required(false)
                .nickName(account.getName())
                .email(account.getUsername())
                .tokenDto(setSecurityContext(account, signupRequest.pushToken()))
                .build();

    }

    @Transactional
    public OAuthSignInResponseDto SNSLogin(OAuthSignInRequestDto request) throws Exception {

        if(request.type() == OAuthType.KAKAO) {
            // 카카오 토큰 가져오기
            KaKaoTokenDto tokenDto = kakaoService.getKakaoToken(request.code());

            // 카카오 유저 정보 가져오기
            KakaoDto kakaoDto = kakaoService.getUserInfoWithToken(tokenDto.accessToken());

            // 이미 가입한 회원인지 확인
            Optional<Account> account = accountRepository.findByUsernameAndIsQuitIsFalse(kakaoDto.getEmail());
            if (account.isEmpty()) {

                // 회원 가입이 필요한 경우
                return OAuthSignInResponseDto.builder()
                        .register_required(true)
                        .nickName(kakaoDto.getNickname())
                        .email(kakaoDto.getEmail())
                        .tokenDto(null)
                        .build();

            } else {

                return OAuthSignInResponseDto.builder().
                        register_required(false)
                        .nickName(kakaoDto.getNickname())
                        .email(kakaoDto.getEmail())
                        .tokenDto(setSecurityContext(account.get(), request.push_token()))
                        .build();
            }
        }
//        else if(request.type() == OAuthType.APPLE) {
//
//        }
//        else if(request.type() == OAuthType.GOOGLE) {
//
//        }
        else {
            throw new NoSuchElementException();
        }

    }

    @Transactional
    public TokenDto setSecurityContext(Account account, String pushToken ){

        OAuthUserDetails oAuthUserDetails= new OAuthUserDetails(account);
        Authentication authentication = new UsernamePasswordAuthenticationToken(oAuthUserDetails, null, oAuthUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 인증 정보를 기반으로 JWT 토큰 생성
        TokenDto jwtTokenDto = tokenProvider.generateTokenDto(authentication);

        // RefreshToken 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .account(account)
                .tokenValue(jwtTokenDto.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);

        // PushToken 저장
        if (!accountPushTokenRepository.existsAccountPushByAccountAndToken(account, pushToken)) {
            AccountPushToken accountPushToken = AccountPushToken.builder()
                    .account(account)
                    .token(pushToken)
                    .build();

            accountPushTokenRepository.save(accountPushToken);
        }

        //인증된 Authentication를 SecurityContext에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtTokenDto;

    }

}
