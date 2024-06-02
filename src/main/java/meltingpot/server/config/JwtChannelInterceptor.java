package meltingpot.server.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meltingpot.server.exception.InvalidTokenException;
import meltingpot.server.util.ResponseCode;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class JwtChannelInterceptor implements ChannelInterceptor {
    private final TokenProvider tokenProvider;
    // 유효성 검사만 해주면 되는지 or setUser 까지 해줘야 하는지

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT == accessor.getCommand()) {
            String jwtToken = accessor.getFirstNativeHeader("Authorization");
            if (jwtToken != null && tokenProvider.validateToken(jwtToken)) {
                Authentication authentication = tokenProvider.getAuthentication(jwtToken);
                accessor.setUser(authentication);
            } else {
                throw new InvalidTokenException(ResponseCode.INVALID_AUTH_TOKEN);
            }
        }
        return message;
    }
}
