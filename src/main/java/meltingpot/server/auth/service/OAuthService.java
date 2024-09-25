package meltingpot.server.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meltingpot.server.auth.controller.dto.OAuthSignInRequestDto;
import meltingpot.server.auth.controller.dto.OAuthSignupRequestDto;
import meltingpot.server.auth.controller.dto.ProfileImageRequestDto;
import meltingpot.server.auth.oauth.OAuthUserDetails;
import meltingpot.server.auth.oauth.OAuthDto;
import meltingpot.server.auth.service.dto.OAuthSignInResponseDto;
import meltingpot.server.config.TokenProvider;
import meltingpot.server.domain.entity.*;
import meltingpot.server.domain.entity.enums.Gender;
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

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
@EnableWebSecurity
public class OAuthService {
    private final AccountRepository accountRepository;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AccountPushTokenRepository accountPushTokenRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

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
                .OAuthType(signupRequest.OauthType())
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

        if(signupRequest.languages()!=null) {
            account.setLanguages(signupRequest.languages().stream().map(
                    (language) -> AccountLanguage.builder()
                            .account(account)
                            .language(language)
                            .build()).toList()
            );
        }


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

        OAuthDto oAuthDto = getUserInfoFromIdToken(request.token());

        // 이미 가입한 회원인지 확인
        Optional<Account> account = accountRepository.findByUsernameAndIsQuitFalseAndOAuthType(oAuthDto.getEmail(), request.type());
        if (account.isEmpty()) {

            // 회원 가입이 필요한 경우
            return OAuthSignInResponseDto.builder()
                    .register_required(true)
                    .nickName(oAuthDto.getNickname())
                    .email(oAuthDto.getEmail())
                    .tokenDto(null)
                    .build();

        } else {
            return OAuthSignInResponseDto.builder().
                    register_required(false)
                    .nickName(oAuthDto.getNickname())
                    .email(oAuthDto.getEmail())
                    .tokenDto(setSecurityContext(account.get(), request.push_token()))
                    .build();
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

    public OAuthDto getUserInfoFromIdToken(String idToken) throws Exception {

        // 온점 분리
        String[] parts = idToken.split("\\.");
        if (parts.length != 3) {
            throw new java.lang.IllegalArgumentException("Invalid IdToken");
        }

        // Payload 디코딩
        String payload = parts[1];
        String decodedPayload = new String(Base64.getDecoder().decode(payload));

        // JSON 파싱
        JsonNode jsonNode = objectMapper.readTree(decodedPayload);

        // email과 nickname 추출
        String email = jsonNode.path("email").asText(null);
        String nickname = jsonNode.path("nickname").asText(null);

        return OAuthDto.builder()
                .email(email)
                .nickname(nickname).build();

    }

}
