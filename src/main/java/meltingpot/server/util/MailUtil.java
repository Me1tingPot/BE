package meltingpot.server.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import meltingpot.server.exception.MailSendException;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

@RequiredArgsConstructor
@Component
public class MailUtil {
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendMimeMessageMailWithValues(String title, String to, String templateName, Map<String, String> values) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // 메일 제목 설정
            helper.setSubject(title);

            // 수신자 설정
            helper.setFrom("멜팅팟 <meltingpot@meltingpot.kr>");
            helper.setTo(to);

            // 템플릿에 전달할 데이터 설정
            Context context = new Context();
            values.forEach(context::setVariable);

            // 메일 내용 설정 : 템플릿 프로세스
            String html = templateEngine.process(templateName, context);
            helper.setText(html, true);

            // 메일 보내기
            javaMailSender.send(message);
        } catch (MessagingException | MailException e) {
            throw new MailSendException(ResponseCode.MAIL_SEND_FAIL);
        }
    }

}
