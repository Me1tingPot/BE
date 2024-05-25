package meltingpot.server.user.controller;

import jakarta.validation.Valid;
import meltingpot.server.user.controller.dto.SigninRequestDto;
import meltingpot.server.user.controller.dto.UserResponseDto;
import meltingpot.server.util.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import meltingpot.server.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("user")
public class UserController {

    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // 회원 가입


    // 로그인
    @PostMapping("signin")
    public ResponseEntity<ResponseData<UserResponseDto>> signin(
            @RequestBody @Valid SigninRequestDto request
    ){


    }


    // 로그아웃

    // 이메일 유효성 확인

    // 비밀번호 재설정

    // 토큰 재발급


}
