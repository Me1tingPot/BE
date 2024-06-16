package meltingpot.server.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import meltingpot.server.auth.controller.dto.ProfileImageRequestDto;
import meltingpot.server.auth.service.AuthService;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.user.controller.dto.NewProfileImageRequestDto;
import meltingpot.server.user.controller.dto.UpdateBioRequestDto;
import meltingpot.server.user.controller.dto.UpdateNameRequestDto;
import meltingpot.server.user.controller.dto.UserResponseDto;
import meltingpot.server.user.service.UserService;
import meltingpot.server.user.service.dto.UserImagesResponseDto;
import meltingpot.server.util.CurrentUser;
import meltingpot.server.util.ResponseCode;
import meltingpot.server.util.ResponseData;
import meltingpot.server.util.r2.FileUploadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
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
    @GetMapping("/images/{accountId}")
    @Operation(summary="마이페이지 사용자 프로필 이미지 조회", description="마이페이지에서 사용자 본인 혹은 다른 사람의 프로필 사진 목록을 불러옵니다.\n" )
    public ResponseEntity<ResponseData<List<UserImagesResponseDto>>> readProfileImages(@PathVariable int accountId){
        try{
            List<UserImagesResponseDto> data = userService.readProfileImages(accountId);
            return ResponseData.toResponseEntity(ResponseCode.READ_PROFILE_SUCCESS, data);

        }catch( NoSuchElementException e ){
            return ResponseData.toResponseEntity(ResponseCode.ACCOUNT_NOT_FOUND, null);
        }
    }

    // 프로필 이미지 URL 생성
    @GetMapping("/image-url")
    @Operation(summary = "마이페이지 프로필 이미지 URL 생성", description = "프로필 이미지 업로드를 위한 URL을 생성합니다. 생성된 URL에 PUT으로 이미지를 업로드 한 뒤 key를 회원가입에 첨부해주세요.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "OK", description = "프로필 이미지 URL 생성 성공")
    })
    public ResponseEntity<ResponseData<FileUploadResponse>> createPartyImageUrl() {
        return ResponseData.toResponseEntity(ResponseCode.IMAGE_URL_GENERATE_SUCCESS, userService.generateImageUploadUrl());
    }

    // 프로필 이미지 추가
    @PostMapping("/images")
    @Operation(summary="마이페이지 사용자 프로필 이미지 추가", description="마이페이지에서 새로운 프로필 사진을 추가합니다.\n" )
    public ResponseEntity<ResponseData> createNewProfileImages(
            @CurrentUser Account account,
            @RequestBody @Valid NewProfileImageRequestDto request
    ){
        try{
            return ResponseData.toResponseEntity(userService.createNewProfileImage(request, account));
        }catch( NoSuchElementException e ){
            return ResponseData.toResponseEntity(ResponseCode.PROFILE_UPDATE_FAIL);
        }
    }

    // 프로필 이미지 삭제
    @DeleteMapping("/images/{imageId}")
    @Operation(summary="마이페이지 사용자 프로필 이미지 삭제", description="마이페이지에서 프로필 사진을 삭제합니다.\n 해당 사진이 대표 사진인 경우 다른 사진을 대표 사진으로 자동 설정합니다." )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "OK", description = "프로필 사진 삭제 성공"),
            @ApiResponse(responseCode = "BAD_REQUEST", description = "프로필 사진이 한 개인 경우 삭제 불가"),
            @ApiResponse(responseCode = "FORBIDDEN", description = "본인이 아닌 경우")
    })
    public ResponseEntity<ResponseData> deleteProfileImage(
            @PathVariable long imageId, @CurrentUser Account account
    ){
        try{
            return ResponseData.toResponseEntity(userService.deleteProfileImage(account, imageId));

        } catch(NoSuchElementException e){
            return ResponseData.toResponseEntity(ResponseCode.PROFILE_IMAGE_DELETE_FAIL);
        }
    }

    // 대표 이미지 변경
    @PutMapping("/images/{imageId}")
    @Operation(summary="마이페이지 사용자 대표이미지 변경", description="마이페이지에서 사용자의 대표 이미지를 ${imageID}로 변경합니다.\n" )
    public ResponseEntity<ResponseData> changeThumbnailImage(
            @PathVariable long imageId, @CurrentUser Account account
    ){
        try{
            return ResponseData.toResponseEntity(userService.changeThumbnailImage(account, imageId));

        } catch(NoSuchElementException e){
            return ResponseData.toResponseEntity(ResponseCode.PROFILE_IMAGE_NOT_FOUND);
        }
    }




    // 프로필 상세 보기

}
