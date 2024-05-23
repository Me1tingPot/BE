package meltingpot.server.chat.service;

import lombok.RequiredArgsConstructor;
import meltingpot.server.domain.repository.ChatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatQueryService {
    private final ChatRepository chatRepository;
}
