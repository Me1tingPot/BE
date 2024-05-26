package meltingpot.server.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import meltingpot.server.chat.dto.*;
import meltingpot.server.chat.service.ChatRoomQueryService;
import meltingpot.server.chat.service.ChatRoomService;
import meltingpot.server.util.PageResponse;
import meltingpot.server.util.ResponseData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static meltingpot.server.util.ResponseCode.SIGNIN_SUCCESS;

@Tag(name="API", description = "Sample API 입니다.")
@RestController
@RequestMapping("/api/v1/chatRooms")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomQueryService chatRoomQueryService;
    private final ChatRoomService chatRoomService;

    // [CHECK] /{userId}, PageResponse, param
    @GetMapping("/{userId}")
    @Operation(summary = "채팅방 전체 목록 조회")
    @ApiResponse(responseCode = "100", description = "요청에 성공하였습니다.", content = @Content(mediaType = "application/json"))
    @Parameter(name = "userId", description = "사용자 ID", required = true, example = "1")
    public ResponseEntity<ResponseData<PageResponse<List<ChatRoomsGetResponse>>>> getChatRooms(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseData.toResponseEntity(SIGNIN_SUCCESS, chatRoomQueryService.getChatRooms());
    }

    // [CHECK] /{userId}
    @PatchMapping("/alarm/{chatRoomId}/{userId}")
    @Operation(summary = "채팅방 알람 설정 변경")
    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content(mediaType = "application/json"))
    @Parameters({
            @Parameter(name = "chatRoomId", description = "채팅룸 ID", required = true, example = "1"),
            @Parameter(name = "userId", description = "사용자 ID", required = true, example = "1")
    })
    public ResponseEntity<ResponseData> updateAlarmStatus(
            @PathVariable Long userId,
            @PathVariable Long chatRoomId
    ) {
        chatRoomService.updateAlarmStatus(userId, chatRoomId);
        return ResponseData.toResponseEntity(SIGNIN_SUCCESS);
    }

    // [CHECK] PageResponse
    @GetMapping("{chatRoomId}")
    @Operation(summary = "채팅방 채팅 내역 조회")
    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content(mediaType = "application/json"))
    @Parameter(name = "chatRoomId", description = "채팅룸 ID", required = true, example = "1")
    public ResponseEntity<ResponseData<PageResponse<List<ChatMessageGetResponse>>>> getChatMessage(
            @PathVariable Long chatRoomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        ChatMessageListQuery query = ChatMessageListQuery.builder()
                .chatRoomId(chatRoomId)
                .page(page)
                .size(size)
                .build();
        return ResponseData.toResponseEntity(SIGNIN_SUCCESS, chatRoomQueryService.getChatMessage(query));
    }

    @GetMapping("{chatRoomId}/detail")
    @Operation(summary = "채팅방 상단")
    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다.", content = @Content(mediaType = "application/json"))
    @Parameter(description = "채팅룸 ID", required = true, example = "1")
    public ResponseEntity<ResponseData<ChatRoomDetailGetResponse>> getChatRoomDetail(@PathVariable Long chatRoomId) {
        return ResponseData.toResponseEntity(SIGNIN_SUCCESS, chatRoomQueryService.getRoomDetail(chatRoomId));
    }
}
