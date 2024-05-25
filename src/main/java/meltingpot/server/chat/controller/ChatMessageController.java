package meltingpot.server.chat.controller;

import lombok.RequiredArgsConstructor;
import meltingpot.server.chat.application.port.in.ChatMessageListQuery;
import meltingpot.server.chat.application.service.usecase.ChatMessageLoadUseCase;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rooms")
public class ChatMessageController {
    private final ChatMessageLoadUseCase chatMessageLoadUseCase;

    @GetMapping("/{chatRoomId}/messages")
    public SuccessApiResponse<?> getMessageList(@PathVariable Long roomId,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "5") int size
    ){
        ChatMessageListQuery query = ChatMessageListQuery.builder()
                .roomId(roomId)
                .page(page)
                .size(size)
                .build();
        return SuccessApiResponse.of(chatMessageLoadUseCase.getChatMessageList(query));
    }
}
