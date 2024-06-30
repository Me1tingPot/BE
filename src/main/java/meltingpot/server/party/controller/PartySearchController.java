package meltingpot.server.party.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import meltingpot.server.party.dto.PartyNearbySearchRequest;
import meltingpot.server.party.dto.PartyResponse;
import meltingpot.server.party.dto.PartySearchRequest;
import meltingpot.server.party.service.PartySearchService;
import meltingpot.server.util.PageResponse;
import meltingpot.server.util.ResponseCode;
import meltingpot.server.util.ResponseData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class PartySearchController {
    private final PartySearchService partySearchService;

    @PostMapping("")
    @Operation(summary = "파티 검색", description = "파티를 검색합니다. TemporalFilter(기간 필터)는 중복 선택이 가능하나, 1주일/한달, 오름차순/내림차순은 중복 선택이 불가능합니다. CoordFilter의 경우 화면 좌측 상단의 좌표, 우측 하단의 좌표를 입력해 해당 화면 내에 존재하는 파티만을 필터할 수 있습니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "OK", description = "파티 검색 성공"),
        @ApiResponse(responseCode = "BAD_REQUEST", description = "파티 검색 실패")
    })
    public ResponseEntity<ResponseData<PageResponse<PartyResponse>>> searchParty(
        @RequestBody @Valid PartySearchRequest partySearchRequest
    ) {
        try {
            return ResponseData.toResponseEntity(ResponseCode.PARTY_SEARCH_SUCCESS, partySearchService.searchParty(partySearchRequest));
        } catch (NoSuchElementException e) {
            return ResponseData.toResponseEntity(ResponseCode.PARTY_SEARCH_FAIL, null);
        } catch (IllegalArgumentException e) {
            return ResponseData.toResponseEntity(ResponseCode.PARTY_INVALID_QUERY, null);
        }
    }

    @PostMapping("/nearby")
    @Operation(summary = "내 주변 파티 검색", description = "사용자의 위치를 기반으로 주변 파티를 검색합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "OK", description = "내 주변 파티 검색 성공"),
        @ApiResponse(responseCode = "BAD_REQUEST", description = "내 주변 파티 검색 실패")
    })
    public ResponseEntity<ResponseData<PageResponse<PartyResponse>>> searchNearbyParty(
        @RequestBody @Valid PartyNearbySearchRequest partyNearbySearchRequest
    ) {
        try {
            return ResponseData.toResponseEntity(ResponseCode.PARTY_SEARCH_SUCCESS, partySearchService.searchNearbyParty(partyNearbySearchRequest));
        } catch (NoSuchElementException e) {
            return ResponseData.toResponseEntity(ResponseCode.PARTY_SEARCH_FAIL, null);
        } catch (IllegalArgumentException e) {
            return ResponseData.toResponseEntity(ResponseCode.PARTY_INVALID_QUERY, null);
        }
    }
}
