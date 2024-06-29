package meltingpot.server.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import meltingpot.server.chat.dto.SocketTokenGetResponse;
import meltingpot.server.chat.service.ChatService;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.util.CurrentUser;
import meltingpot.server.util.ResponseCode;
import meltingpot.server.util.ResponseData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/token")
    @Operation(summary = "소켓 전용 토큰 발급", description = "사용자가 채팅방에 입장하기 전에, 소켓 전용 토큰 발급")
    public ResponseEntity<ResponseData<SocketTokenGetResponse>> getToken(@CurrentUser Account account) {
        SocketTokenGetResponse data = chatService.getToken(account);

        return ResponseData.toResponseEntity(ResponseCode.SOCKET_TOKEN_GET_SUCCESS, data);
    }
}
