package meltingpot.server.chat.service;

import lombok.RequiredArgsConstructor;
import meltingpot.server.chat.dto.SocketTokenGetResponse;
import meltingpot.server.config.TokenProvider;
import meltingpot.server.domain.entity.Account;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final TokenProvider tokenProvider;

    public SocketTokenGetResponse getToken(Account account) {
        String socketToken = tokenProvider.generateSocketToken(account);

        return new SocketTokenGetResponse(socketToken);
    }
}
