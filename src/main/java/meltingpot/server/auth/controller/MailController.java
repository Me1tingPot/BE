package meltingpot.server.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import meltingpot.server.auth.controller.dto.MailVerificationRequestDto;
import meltingpot.server.auth.controller.dto.VerificationCodeRequestDto;
import meltingpot.server.auth.service.MailService;
import meltingpot.server.exception.DuplicateException;
import meltingpot.server.exception.MailVerificationException;
import meltingpot.server.util.ResponseCode;
import meltingpot.server.util.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/mail")
public class MailController {
    private final MailService mailService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // 정회원 인증 메일 전송
    @PostMapping("")
    @Operation(summary="이메일 인증번호 전송", description="입력 받은 이메일로 인증 번호를 전송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "MAIL_VERIFICATION_SEND_SUCCESS", description = "이메일 인증번호 전송 성공"),
            @ApiResponse(responseCode = "VERIFICATION_CODE_ALREADY_EXIST", description = "이미 생성한 인증 번호가 있습니다"),
            @ApiResponse(responseCode = "MAIL_SEND_FAIL", description = "이메일 전송 실패")
    })
    public ResponseEntity<ResponseData> sendVerificationMail(
            @RequestBody @Valid MailVerificationRequestDto request
    ) {
        try{
            logger.info("MAIL_VERIFICATION_SEND_SUCCESS (200 OK)");
            return ResponseData.toResponseEntity(mailService.sendVerificationMail(request));

        }catch(MailVerificationException e){
            return ResponseData.toResponseEntity(e.getResponseCode());
        }
    }

    @PostMapping("verification")
    @Operation(summary="이메일 인증번호 확인", description="이메일 인증 번호를 입력 받고 올바른 번호인지 확인합니다." )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "MAIL_VERIFICATION_CHECK_SUCCESS", description = "인증번호가 일치합니다"),
            @ApiResponse(responseCode = "AUTHENTICATION_NOT_FOUND", description = "메일 인증 정보를 찾을 수 없습니다"),
            @ApiResponse(responseCode = "AUTH_TIME_OUT", description = "인증 시간을 초과했습니다"),
            @ApiResponse(responseCode = "AUTH_NUMBER_INCORRECT", description = "인증 번호가 틀렸습니다"),
    })
    public ResponseEntity<ResponseData> checkVerification(
            @RequestBody @Valid VerificationCodeRequestDto request
    ) {
        try{
            logger.info("VERIFICATION_CHECK_SUCCESS (200 OK)");
        return ResponseData.toResponseEntity( mailService.checkVerification(request));

        }catch(MailVerificationException e){
            return ResponseData.toResponseEntity(e.getResponseCode());
        }
    }

    // 이메일 중복 확인
    @PostMapping("duplication")
    @Operation(summary="이메일 중복 체크", description="이미 가입한 이메일인지 확인하는 API 입니다.\n" )
    public ResponseEntity<ResponseData> checkEmail(
            @RequestBody @Valid MailVerificationRequestDto request) {
        try{
            mailService.checkUserName(request.email());
            return ResponseData.toResponseEntity(ResponseCode.MAIL_AVAILABLE);
        }catch (DuplicateException e){
            return ResponseData.toResponseEntity(ResponseCode.EMAIL_DUPLICATION);
        }
    }
}
