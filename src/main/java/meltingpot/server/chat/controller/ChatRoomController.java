package meltingpot.server.chat.controller;

import lombok.RequiredArgsConstructor;
import meltingpot.server.chat.dto.ChatMessagesGetResponse;
import meltingpot.server.chat.dto.ChatRoomAlarmUpdateRequest;
import meltingpot.server.chat.dto.ChatRoomDetailGetResponse;
import meltingpot.server.chat.dto.ChatRoomsGetResponse;
import meltingpot.server.chat.service.ChatRoomService;
import meltingpot.server.util.ResponseData;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chatRooms")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @GetMapping
    public ResponseData<ChatRoomsGetResponse> getChatRooms() {
        return null;
    }

    @PatchMapping("/alarm")
    public ResponseData<ChatRoomAlarmUpdateRequest> updateAlarmStatus() {
        return null;
    }

    @GetMapping("{chatRoomId}")
    public ResponseData<ChatMessagesGetResponse> getChatMessage() {
        return null;
    }

    @GetMapping("{chatRoomId}/detail")
    public ResponseData<ChatRoomDetailGetResponse> getChatRoomDetail() {
        return null;
    }
}
