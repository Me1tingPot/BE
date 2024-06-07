package meltingpot.server.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import meltingpot.server.auth.service.AuthService;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.user.controller.dto.UpdateBioRequestDto;
import meltingpot.server.user.controller.dto.UpdateNameRequestDto;
import meltingpot.server.user.controller.dto.UserResponseDto;
import meltingpot.server.user.service.UserService;
import meltingpot.server.user.service.dto.UserImagesResponseDto;
import meltingpot.server.util.CurrentUser;
import meltingpot.server.util.ResponseCode;
import meltingpot.server.util.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@RestController
@RequestMapping("user")
public class UserController {
    private final UserService userService;
    private  final AuthService authService;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    // 프로필 조회
    @GetMapping()
    @Operation(summary="마이페이지 사용자 기본 프로필 정보", description="마이페이지에서 사용자 기본 프로필 정보를 불러옵니다.\n" )
    public ResponseEntity<ResponseData<UserResponseDto>> readProfile(@CurrentUser Account account){
        UserResponseDto data = userService.readProfile(account);
        if( account != null ){
            logger.info("READ_USER_SUCCESS (200 OK) :: userId = {}", data.getId());
        }
        return ResponseData.toResponseEntity(ResponseCode.READ_PROFILE_SUCCESS, data);
    }

    // 프로필 이름 수정
    @PatchMapping("/name")
    @Operation(summary="프로필 닉네임 수정", description="프로필 수정 - 사용자 닉네임 수정\n" )
    public ResponseEntity<ResponseData<UserResponseDto>> updateProfileName(
            @CurrentUser Account account, @Valid @RequestBody UpdateNameRequestDto request ){
        UserResponseDto data = userService.updateProfileName(account, request.toServiceDto());
        if (account != null) {
            logger.info("UPDATE_NICKNAME_SUCCESS (200 OK) :: userId = {}", data.getId());
        }
        return ResponseData.toResponseEntity(ResponseCode.UPDATE_NICKNAME_SUCCESS, data);
    }

    // 프로필 소개 수정
    @PatchMapping("/bio")
    @Operation(summary="프로필 소개 수정", description="프로필 수정 - 사용자 소개 수정\n" )
    public ResponseEntity<ResponseData<UserResponseDto>> updateProfileBio(
            @CurrentUser Account account, @Valid @RequestBody UpdateBioRequestDto request ){
        UserResponseDto data = userService.updateProfileBio(account, request.toServiceDto());
        if (account != null) {
            logger.info("UPDATE_NICKNAME_SUCCESS (200 OK) :: userId = {}", data.getId());
        }
        return ResponseData.toResponseEntity(ResponseCode.UPDATE_NICKNAME_SUCCESS, data);
    }

    // 사용자 프로필 이미지 조회
    @GetMapping("/{accountId}")
    @Operation(summary="마이페이지 사용자 프로필 이미지 조회", description="마이페이지에서 사용자 본인 혹은 다른 사람의 프로필 사진 목록을 불러옵니다.\n" )
    public ResponseEntity<ResponseData<List<UserImagesResponseDto>>> readProfileImages(@PathVariable int accountId){
        try{
            List<UserImagesResponseDto> data = userService.readProfileImages(accountId);

            return ResponseData.toResponseEntity(ResponseCode.READ_PROFILE_SUCCESS, data);

        }catch( NoSuchElementException e ){
            return ResponseData.toResponseEntity(ResponseCode.ACCOUNT_NOT_FOUND, null);
        }
    }



    // 대표 이미지 변경


    // 프로필 이미지 수정 및 삭제


    // 프로필 상세 보기




}
