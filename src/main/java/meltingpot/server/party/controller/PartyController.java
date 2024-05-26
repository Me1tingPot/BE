package meltingpot.server.party.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.party.dto.PartyResponse;
import meltingpot.server.party.service.PartyService;
import meltingpot.server.util.CurrentUser;
import meltingpot.server.util.ResponseCode;
import meltingpot.server.util.ResponseData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/party")
@RequiredArgsConstructor
public class PartyController {
    private final PartyService partyService;

    @GetMapping("/{partyId}")
    @Operation(summary = "파티 정보 조회", description = "파티 ID를 통해 파티 정보를 불러옵니다. 임시 저장된 파티를 불러오는 경우 작성자만 불러올 수 있습니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "OK", description = "파티 정보 조회 성공"),
        @ApiResponse(responseCode = "NOT_FOUND", description = "파티 정보를 찾을 수 없습니다")
    })
    public ResponseEntity<ResponseData<PartyResponse>> getParty(@PathVariable int partyId, @CurrentUser Account user) {
        try {
            return ResponseData.toResponseEntity(ResponseCode.PARTY_FETCH_SUCCESS, partyService.getParty(partyId, user));
        } catch (NoSuchElementException e) {
            return ResponseData.toResponseEntity(ResponseCode.PARTY_NOT_FOUND, null);
        }
    }

    @PostMapping("/{partyId}/join")
    @Operation(summary = "파티 참여", description = "파티 ID를 통해 특정 파티에 참여")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "OK", description = "파티 참여 성공"),
        @ApiResponse(responseCode = "NOT_FOUND", description = "파티 정보를 찾을 수 없습니다"),
        @ApiResponse(responseCode = "BAD_REQUEST", description = "파티 참여 실패 (이미 참여한 파티, 최대 인원 초과, 모집중이 아닌 파티 등)")
    })
    public ResponseEntity<ResponseData> joinParty(@PathVariable int partyId, @CurrentUser Account user) {
        try {
            return ResponseData.toResponseEntity(partyService.joinParty(partyId, user));
        } catch (NoSuchElementException e) {
            return ResponseData.toResponseEntity(ResponseCode.PARTY_NOT_FOUND);
        }
    }
}
