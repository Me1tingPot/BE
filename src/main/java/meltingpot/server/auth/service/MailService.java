package meltingpot.server.auth.service;

import lombok.RequiredArgsConstructor;
import meltingpot.server.auth.controller.dto.MailVerificationRequestDto;
import meltingpot.server.auth.controller.dto.VerificationCodeRequestDto;
import meltingpot.server.domain.entity.Constants;
import meltingpot.server.domain.entity.MailVerification;
import meltingpot.server.domain.repository.AccountRepository;
import meltingpot.server.domain.repository.MailVerificationRepository;
import meltingpot.server.exception.DuplicateException;
import meltingpot.server.exception.MailVerificationException;
import meltingpot.server.util.MailUtil;
import meltingpot.server.util.ResponseCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class MailService {
    private final MailVerificationRepository mailVerificationRepository;
    private final MailUtil mailUtil;
    private final AccountRepository accountRepository;

    // 인증 코드 생성
    private String createCode() {
        try {
            Random rand = SecureRandom.getInstanceStrong();
            StringBuilder code = new StringBuilder();

            for (int i = 0; i < 6; i++) {
                // 0~9까지 난수 생성
                String num = Integer.toString(rand.nextInt(10));
                code.append(num);
            }

            return code.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 인증 메일 발송
    @Transactional
    public ResponseCode sendVerificationMail(MailVerificationRequestDto request) {
        String email = request.email();

        // 이미 유효한 인증 정보가 있는 경우
        Optional<MailVerification> oldMailVerification = mailVerificationRepository.findByEmailAndExpiredAtIsAfterNowAndVerifiedFalse(email,LocalDateTime.now());
        if(oldMailVerification.isPresent()){
            throw new MailVerificationException(ResponseCode.VERIFICATION_CODE_ALREADY_EXIST);
        }

        String code = createCode();
        Map<String,String> mailValues = Map.of("code", code);

        String title = "[멜팅팟] 이메일 인증을 위한 인증 번호 안내";

        // 메일 전송
        mailUtil.sendMimeMessageMailWithValues(title, email, "EmailAuthenticationForm.html", mailValues);

        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(Constants.AUTH_TIME_LIMIT);

        MailVerification mailVerification = MailVerification.builder()
                .email(email)
                .expiredAt(expiredAt)
                .authenticationNumber(code)
                .build();

        mailVerificationRepository.save(mailVerification);

        return ResponseCode.MAIL_VERIFICATION_SEND_SUCCESS;

    }


    // 인증 번호 확인
    @Transactional
    public ResponseCode checkVerification(VerificationCodeRequestDto request) {

        // 현재 유효한 인증 정보 가져오기
        MailVerification mailVerification = mailVerificationRepository.findByEmailAndExpiredAtIsAfterNowAndVerifiedFalse(request.email(), LocalDateTime.now())
                .orElseThrow( // 인증 정보가 없는 경우
                        ()->new MailVerificationException(ResponseCode.AUTHENTICATION_NOT_FOUND)
                );

        // 인증 번호 유효 기간을 초과한 경우
        if(LocalDateTime.now().isAfter(mailVerification.getExpiredAt())){
            throw new MailVerificationException(ResponseCode.AUTH_TIME_OUT);
        }

        // 인증 번호가 틀린 경우
        if(!mailVerification.getAuthenticationNumber().equals(request.code())){
            throw new MailVerificationException(ResponseCode.AUTH_NUMBER_INCORRECT);
        }

        // 인증에 성공한 경우: 제한 시간 내에 인증 번호를 올바르게 입력한 경우
        mailVerification.setVerified(true);
        mailVerificationRepository.save(mailVerification);

        return ResponseCode.MAIL_VERIFICATION_CHECK_SUCCESS;

    }

    // 회원가입시 이메일 유효성 확인
    @Transactional(readOnly = true)
    public void checkUserName(String username) {
        if(accountRepository.existsByUsername(username)){
            throw new DuplicateException(ResponseCode.EMAIL_DUPLICATION);
        }
    }

}
