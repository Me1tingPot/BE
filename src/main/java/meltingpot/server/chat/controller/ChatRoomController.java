package meltingpot.server.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meltingpot.server.chat.dto.*;
import meltingpot.server.chat.service.ChatRoomQueryService;
import meltingpot.server.chat.service.ChatRoomService;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.util.CurrentUser;
import meltingpot.server.util.ResponseData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static meltingpot.server.util.ResponseCode.*;

@Slf4j
@Tag(name="chatroom-controller", description = "채팅방 API")
@RestController
@RequestMapping("/api/v1/chatRooms")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomQueryService chatRoomQueryService;
    private final ChatRoomService chatRoomService;

    // [CHECK] 1. Post @RequestBody or 2. Get @ModelAttribute
    @GetMapping()
    @Operation(summary = "채팅방 전체 목록 조회", description = "사용자가 참여하는 전체 채팅방 조회. 파티에 참여하면 채팅방 자동으로 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "OK", description = "채팅방 전체 목록 조회 성공"),
            @ApiResponse(responseCode = "NOT_FOUND", description = "채팅방 정보를 찾을 수 없습니다"),
            @ApiResponse(responseCode = "BAD_REQUEST", description = "채팅방 전체 목록 조회 실패")
    })
    public ResponseEntity<ResponseData<ChatRoomsPageResponse>> getChatRooms(
            @CurrentUser Account user,
            @RequestParam(name = "page") int page,
            @RequestParam(name = "size") int size
    ) {
        return ResponseData.toResponseEntity(CHAT_ROOMS_LIST_GET_SUCCESS, chatRoomQueryService.getChatRooms(user.getId(), page, size));
    }

    @PostMapping("/alarm/{chatRoomId}")
    @Operation(summary = "채팅방 알림 설정 변경", description = "채팅방 전체 목록에서 각 채팅방의 알림 (ON / OFF) 설정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "OK", description = "채팅방 알림 설정 변경 성공"),
            @ApiResponse(responseCode = "NOT_FOUND", description = "채팅방 정보를 찾을 수 없습니다"),
            @ApiResponse(responseCode = "BAD_REQUEST", description = "채팅방 알림 설정 변경 실패")
    })
    public ResponseEntity<ResponseData> updateAlarmStatus(
            @CurrentUser Account user,
            @PathVariable("chatRoomId") Long chatRoomId
    ) {
        return ResponseData.toResponseEntity(chatRoomService.updateAlarmStatus(user.getId(), chatRoomId));
    }

    // [CHECK] PageResponse
    @GetMapping("/chat/{chatRoomId}")
    @Operation(summary = "채팅방 채팅 내역 조회", description = "채팅방 입장 후, 채팅방 메시지 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "OK", description = "채팅방 채팅 내역 조회 성공"),
            @ApiResponse(responseCode = "NOT_FOUND", description = "채팅방 정보를 찾을 수 없습니다"),
            @ApiResponse(responseCode = "BAD_REQUEST", description = "채팅방 메세지 조회 실패")
    })
    public ResponseEntity<ResponseData<ChatMessagePageResponse>> getChatMessage(
            @PathVariable("chatRoomId") Long chatRoomId,
            @RequestParam(name = "page") int page,
            @RequestParam(name = "size") int size
            ) {
        return ResponseData.toResponseEntity(CHAT_MESSAGE_GET_SUCCESS, chatRoomQueryService.getChatMessages(chatRoomId, page, size));
    }
}
