package meltingpot.server.config;

import lombok.extern.slf4j.Slf4j;
import meltingpot.server.util.ErrorCode;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class ChatErrorHandler extends StompSubProtocolErrorHandler {
    // Interceptor 통해 jwt 검사하는 과정에서 에러 발생한 경우, 에러 핸들링
    // 메시지 객체 만들어서 반환 (메시지 안에 에러 내용 담아서 반환하면, 클라이언트에서 에러 내용 처리해주도록)

    public ChatErrorHandler() {
        super();
    }

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        if(ex.getCause().getMessage().equals("jwt")) {
            return jwtException(clientMessage, ex);
        }

        if(ex.getCause().getMessage().equals("error")) {
            return messageException(clientMessage, ex);
        }

        return super.handleClientMessageProcessingError(clientMessage, ex);
    }

    // 메시지 예외
    private Message<byte[]> messageException(Message<byte[]> clientMessage, Throwable ex) {
        return errorMessage(ErrorCode.INVALID_MESSAGE);
    }

    // JWT 예외
    private Message<byte[]> jwtException(Message<byte[]> clientMessage, Throwable ex) {
        return errorMessage(ErrorCode.INVALID_TOKEN);
    }

    // 메시지 생성
    private Message<byte[]> errorMessage(ErrorCode errorCode) {
        String code = String.valueOf(errorCode.getMessage());

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);

        accessor.setMessage(String.valueOf(errorCode.getStatus()));
        accessor.setLeaveMutable(true);

        return MessageBuilder.createMessage(code.getBytes(StandardCharsets.UTF_8), accessor.getMessageHeaders());
    }
}
