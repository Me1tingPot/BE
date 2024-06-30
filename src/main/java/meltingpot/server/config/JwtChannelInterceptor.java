package meltingpot.server.config;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.chat.ChatRoom;
import meltingpot.server.domain.entity.chat.ChatRoomUser;
import meltingpot.server.domain.repository.AccountRepository;
import meltingpot.server.domain.repository.chat.ChatRoomUserRepository;
import meltingpot.server.exception.*;
import meltingpot.server.util.ResponseCode;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static meltingpot.server.util.ResponseCode.*;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final String PUB_MESSAGE_PREFIX = "/pub/";
    private final String SUB_MESSAGE_PREFIX = "/sub/";

    private final TokenProvider tokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(headerAccessor.getCommand())) {

            List<String> authorization = headerAccessor.getNativeHeader("Authorization");
            if (authorization == null || authorization.size() != 1) {
                throw new BadRequestException(AUTHORIZATION_CHECK_FAIL);
            }
            String accessToken = authorization.get(0).substring(7);

            if (tokenProvider.validateToken(accessToken)) {
                Authentication authentication = tokenProvider.getAuthentication(accessToken);

                SecurityContextHolder.getContext().setAuthentication(authentication);
                headerAccessor.setUser(authentication);
            } else {
                throw new InvalidTokenException(ResponseCode.INVALID_AUTH_TOKEN);
            }
        }

        if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())) {
            String destination = headerAccessor.getDestination();

            if (destination == null || !destination.startsWith(SUB_MESSAGE_PREFIX)) {
                throw new BadRequestException(DESTINATION_NOT_VALID);
            }
        }

        if (StompCommand.SEND.equals(headerAccessor.getCommand())) {
            String destination = headerAccessor.getDestination();

            if (destination == null || !destination.startsWith(PUB_MESSAGE_PREFIX)) {
                throw new BadRequestException(DESTINATION_NOT_VALID);
            }
        }
        return message;
    }
}
