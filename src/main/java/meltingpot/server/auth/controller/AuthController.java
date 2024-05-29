package meltingpot.server.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import meltingpot.server.auth.controller.dto.SigninRequestDto;
import meltingpot.server.auth.controller.dto.AccountResponseDto;
import meltingpot.server.util.ResponseCode;
import meltingpot.server.util.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import meltingpot.server.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("auth")
public class AuthController {

    private final AuthService authService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // 회원 가입


    // 로그인
    @PostMapping("signin")
    @Operation(summary="로그인", description="로그인 API 입니다. 결과로 반환되는 accessToken을 복사해 Authorize에 입력해주세요\n" )
    public ResponseEntity<ResponseData<AccountResponseDto>> signin(
            @RequestBody @Valid SigninRequestDto request
    ){
        AccountResponseDto data = authService.signin(request.toServiceDto());
        logger.info("SIGNIN_SUCCESS (200 OK) :: userId = {}, userEmail = {}",
                data.getId(), data.getEmail());
        return ResponseData.toResponseEntity(ResponseCode.SIGNIN_SUCCESS, data);
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


    // 이메일 인증

    // 비밀번호 재설정

    // 토큰 재발급


}
