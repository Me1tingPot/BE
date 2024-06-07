package meltingpot.server.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import meltingpot.server.auth.controller.dto.*;
import meltingpot.server.exception.InvalidTokenException;
import meltingpot.server.util.ResponseCode;
import meltingpot.server.util.ResponseData;
import meltingpot.server.util.r2.FileUploadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import meltingpot.server.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("auth")
public class AuthController {

    private final AuthService authService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // 회원 가입
    @PostMapping("signup")
    @Operation(summary="회원가입", description="회원가입 API 입니다.\n 회원가입 성공시 자동 로그인되어 AccessToken이 반환됩니다. " )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "CREATED", description = "회원가입 성공"),
            @ApiResponse(responseCode = "BAD_REQUEST", description = "회원가입 실패")
    })
    public ResponseEntity<ResponseData<AccountResponseDto>> signup(
            @RequestBody @Valid SignupRequestDto request
    ){
        AccountResponseDto data = authService.signup(request);
        logger.info("SIGNUP_SUCCESS (200 OK) :: userId = {}, userEmail = {}",
                data.getId(), data.getEmail());
        return ResponseData.toResponseEntity(ResponseCode.SIGNUP_SUCCESS, data);
    }

    // 프로필 이미지 URL 생성
    @GetMapping("/image-url")
    @Operation(summary = "프로필 이미지 URL 생성", description = "프로필 이미지 업로드를 위한 URL을 생성합니다. 생성된 URL에 PUT으로 이미지를 업로드 한 뒤 key를 회원가입에 첨부해주세요.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "OK", description = "프로필 이미지 URL 생성 성공")
    })
    public ResponseEntity<ResponseData<FileUploadResponse>> createPartyImageUrl() {
        return ResponseData.toResponseEntity(ResponseCode.IMAGE_URL_GENERATE_SUCCESS, authService.generateImageUploadUrl());
    }


    // 로그인
    @PostMapping("signin")
    @Operation(summary="로그인", description="로그인 API 입니다. 결과로 반환되는 accessToken을 복사해 Authorize에 입력해주세요\n" )
    public ResponseEntity<ResponseData<AccountResponseDto>> signin(
            @RequestBody @Valid SigninRequestDto request
    ){
        try{
            AccountResponseDto data = authService.signin(request.toServiceDto());
            logger.info("SIGNIN_SUCCESS (200 OK) :: userId = {}, userEmail = {}",
                    data.getId(), data.getEmail());
            return ResponseData.toResponseEntity(ResponseCode.SIGNIN_SUCCESS, data);

        }catch( NoSuchElementException e ){
            return ResponseData.toResponseEntity(ResponseCode.ACCOUNT_NOT_FOUND, null);
        }
    }


    // 로그아웃
    @GetMapping("signout")
    @Operation(summary="로그아웃", description="로그아웃 API 입니다.\n" )
    public ResponseEntity<ResponseData> signout(
            @RequestParam("refresh-token") @NotBlank String refreshToken) {
        authService.signout(refreshToken);
        logger.info("SIGNOUT_SUCCESS (200 OK)");
        return ResponseData.toResponseEntity(ResponseCode.SIGNOUT_SUCCESS);
    }

    // 토큰 재발급
    @PostMapping("reissue-token")
    @Operation(summary="토큰 재발급", description="AccessToken과 RefreshToken을 받으면 두 토큰을 새로 재발급해주는 API 입니다.\n" )
    public ResponseEntity<ResponseData<ReissueTokenResponseDto>> reissueToken(
            @RequestHeader("Authorization") String accessToken,
            @RequestHeader("RefreshToken") String refreshToken
    ) {
        try{
            return ResponseData.toResponseEntity(ResponseCode.REISSUE_TOKEN_SUCCESS, authService.reissueToken(accessToken, refreshToken));
        }
        catch ( InvalidTokenException e ){
            return ResponseData.toResponseEntity(ResponseCode.INVALID_REFRESH_TOKEN, null);
        }
    }



    // 이메일 인증

    // 비밀번호 재설정

    // 탈퇴


}
