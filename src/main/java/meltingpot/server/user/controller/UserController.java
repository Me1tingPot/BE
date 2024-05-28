package meltingpot.server.user.controller;

import lombok.RequiredArgsConstructor;
import meltingpot.server.auth.service.AuthService;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.user.controller.dto.UserResponseDto;
import meltingpot.server.user.service.UserService;
import meltingpot.server.util.CurrentUser;
import meltingpot.server.util.ResponseCode;
import meltingpot.server.util.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("user")
public class UserController {
    private final UserService userService;
    private  final AuthService authService;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    // 프로필 조회
    @GetMapping()
    public ResponseEntity<ResponseData<UserResponseDto>> readProfile(@CurrentUser Account account){
        UserResponseDto data = userService.readProfile(account);
        if( account != null ){
            logger.info("READ_USER_SUCCESS (200 OK) :: userId = {}", data.getId());
        }
        return ResponseData.toResponseEntity(ResponseCode.READ_PROFILE_SUCCESS, data);
    }
}
