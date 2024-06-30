package meltingpot.server.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meltingpot.server.chat.dto.ChatMessageSendRequest;
import meltingpot.server.chat.service.WebSocketService;
import meltingpot.server.domain.entity.chat.ChatMessage;
import meltingpot.server.domain.entity.chat.SocketSession;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WebSocketController {
    private final WebSocketService webSocketService;

    @MessageMapping("/chat")
    public void sendMessage(ChatMessageSendRequest chatMessageSendRequest, StompHeaderAccessor headerAccessor) {
        ChatMessage chatMessage = webSocketService.createChatMessage(headerAccessor.getSessionId(), chatMessageSendRequest);

        webSocketService.convertAndSend(chatMessage);
    }

    @EventListener(SessionConnectEvent.class)
    public void onConnect(SessionConnectEvent event){
        final MessageHeaders headers = event.getMessage().getHeaders();

        SocketSession socketSession = webSocketService.onConnect(headers);

        log.info("sessionId = {}", socketSession.getSessionId());
    }

    @EventListener(SessionSubscribeEvent.class)
    public void onSubscribe(SessionSubscribeEvent event){
        final MessageHeaders headers = event.getMessage().getHeaders();

        SocketSession socketSession = webSocketService.onSubscribe(headers);

        log.info("sessionId = {}", socketSession.getSessionId());
    }


    @EventListener(SessionDisconnectEvent.class)
    public void onDisconnect(SessionDisconnectEvent event) {
        webSocketService.onDisconnect(event.getSessionId());

        log.info("sessionId = {}", event.getSessionId());
    }
}