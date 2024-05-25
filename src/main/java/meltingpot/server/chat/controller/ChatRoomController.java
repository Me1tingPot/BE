package meltingpot.server.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import meltingpot.server.chat.dto.ChatMessageCreateResponse;
import meltingpot.server.chat.dto.ChatRoomDetailGetResponse;
import meltingpot.server.chat.dto.ChatRoomsGetResponse;
import meltingpot.server.chat.service.ChatRoomQueryService;
import meltingpot.server.chat.service.ChatRoomService;
import meltingpot.server.util.ResponseData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static meltingpot.server.util.ResponseCode.SIGNIN_SUCCESS;

@RestController
@RequestMapping("/api/v1/chatRooms")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomQueryService chatRoomQueryService;
    private final ChatRoomService chatRoomService;

    // [CHECK] /{userId}
    @GetMapping("/{userId}")
    @Operation(summary = "채팅방 전체 목록 조회")
    public ResponseEntity<ResponseData<List<ChatRoomsGetResponse>>> getChatRooms(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseData.toResponseEntity(SIGNIN_SUCCESS, chatRoomQueryService.getChatRooms(userId, page, size));
    }

    // [CHECK] /{userId}
    @PatchMapping("/alarm/{chatRoomId}/{userId}")
    @Operation(summary = "채팅방 알람 설정 변경")
    public ResponseEntity<ResponseData> updateAlarmStatus(
            @PathVariable Long userId,
            @PathVariable Long chatRoomId
    ) {
        chatRoomService.updateAlarmStatus(userId, chatRoomId);
        return ResponseData.toResponseEntity(SIGNIN_SUCCESS);
    }

    @GetMapping("{chatRoomId")
    @Operation(summary = "채팅방 채팅 내역 조회")
    public ResponseEntity<ResponseData<List<ChatMessageCreateResponse>>> getChatMessage(
            @PathVariable Long chatRoomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseData.toResponseEntity(SIGNIN_SUCCESS, chatRoomQueryService.getMessage(chatRoomId));
    }

    @GetMapping("{roomId}/detail")
    @Operation(summary = "채팅방 상단")
    public ResponseEntity<ResponseData<ChatRoomDetailGetResponse>> getChatRoomDetail(@PathVariable Long roomId) {
        return ResponseData.toResponseEntity(SIGNIN_SUCCESS, chatRoomQueryService.getRoomDetail(roomId));
    }
}
