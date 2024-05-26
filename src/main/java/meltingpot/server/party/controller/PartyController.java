package meltingpot.server.party.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import meltingpot.server.party.dto.PartyResponse;
import meltingpot.server.party.service.PartyService;
import meltingpot.server.util.ResponseCode;
import meltingpot.server.util.ResponseData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/party")
@RequiredArgsConstructor
public class PartyController {
    private final PartyService partyService;

    // Todo: 사용자 인증 추가
    @GetMapping("/{partyId}")
    @Operation(summary = "파티 정보 조회", description = "파티 ID를 통해 파티 정보를 불러옵니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "OK", description = "파티 정보 조회 성공"),
        @ApiResponse(responseCode = "NOT_FOUND", description = "파티 정보를 찾을 수 없습니다")
    })
    public ResponseEntity<ResponseData<PartyResponse>> getParty(@PathVariable Long partyId) {
        try {
            return ResponseData.toResponseEntity(ResponseCode.PARTY_FETCH_SUCCESS, partyService.getParty(partyId));
        } catch (NoSuchElementException e) {
            return ResponseData.toResponseEntity(ResponseCode.PARTY_NOT_FOUND, null);
        }
    }
}
