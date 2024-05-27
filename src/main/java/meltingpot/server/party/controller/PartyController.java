package meltingpot.server.party.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.party.dto.PartyCreateRequest;
import meltingpot.server.party.dto.PartyReportRequest;
import meltingpot.server.party.dto.PartyResponse;
import meltingpot.server.party.service.PartyService;
import meltingpot.server.util.CurrentUser;
import meltingpot.server.util.ResponseCode;
import meltingpot.server.util.ResponseData;
import meltingpot.server.util.r2.FileUploadResponse;
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

    @GetMapping("/temp-saved")
    @Operation(summary = "임시 저장된 파티 조회", description = "사용자가 임시 저장한 파티 정보를 조회합니다. * 임시 저장된 파티는 1회만 불러올 수 있으며, 이후에는 삭제됩니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "OK", description = "임시 저장된 파티 조회 성공"),
        @ApiResponse(responseCode = "NOT_FOUND", description = "임시 저장된 파티 정보를 찾을 수 없습니다")
    })
    public ResponseEntity<ResponseData<PartyResponse>> getTempSavedParty(@CurrentUser Account user) {
        try {
            return ResponseData.toResponseEntity(ResponseCode.PARTY_FETCH_SUCCESS, partyService.getTempSavedParty(user));
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

    @PostMapping("/{partyId}/report")
    @Operation(summary = "파티 신고", description = "파티 ID를 통해 특정 파티를 신고")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "CREATED", description = "파티 신고 접수 완료"),
        @ApiResponse(responseCode = "NOT_FOUND", description = "파티 정보를 찾을 수 없습니다"),
        @ApiResponse(responseCode = "BAD_REQUEST", description = "파티 신고 실패 (이미 신고한 파티, 작성자가 신고하는 경우 등)")
    })
    public ResponseEntity<ResponseData> reportParty(@PathVariable int partyId, @CurrentUser Account user, @RequestBody @Valid PartyReportRequest partyReportRequest) {
        try {
            return ResponseData.toResponseEntity(partyService.reportParty(partyId, user, partyReportRequest));
        } catch (NoSuchElementException e) {
            return ResponseData.toResponseEntity(ResponseCode.PARTY_NOT_FOUND);
        }
    }

    @DeleteMapping("/{partyId}")
    @Operation(summary = "파티 삭제", description = "파티 ID를 통해 특정 파티를 삭제")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "NO_CONTENT", description = "파티 삭제 성공"),
        @ApiResponse(responseCode = "NOT_FOUND", description = "파티 정보를 찾을 수 없습니다"),
        @ApiResponse(responseCode = "BAD_REQUEST", description = "파티 삭제 실패 (작성자가 아닌 경우 등)")
    })
    public ResponseEntity<ResponseData> deleteParty(@PathVariable int partyId, @CurrentUser Account user) {
        try {
            return ResponseData.toResponseEntity(partyService.deleteParty(user, partyId));
        } catch (NoSuchElementException e) {
            return ResponseData.toResponseEntity(ResponseCode.PARTY_NOT_FOUND);
        }
    }

    @PutMapping("/{partyId}")
    @Operation(summary = "파티 수정", description = "파티 ID를 통해 특정 파티를 수정")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "NO_CONTENT", description = "파티 수정 성공"),
        @ApiResponse(responseCode = "NOT_FOUND", description = "파티 정보를 찾을 수 없습니다"),
        @ApiResponse(responseCode = "BAD_REQUEST", description = "파티 수정 실패 (작성자가 아닌 경우 등)")
    })
    public ResponseEntity<ResponseData> updateParty(@PathVariable int partyId, @CurrentUser Account user, @RequestBody @Valid PartyCreateRequest partyCreateRequest) {
        try {
            return ResponseData.toResponseEntity(partyService.modifyParty(user, partyId, partyCreateRequest));
        } catch (NoSuchElementException e) {
            return ResponseData.toResponseEntity(ResponseCode.PARTY_NOT_FOUND);
        }
    }

    @GetMapping("/image-url")
    @Operation(summary = "파티 이미지 URL 생성", description = "파티 이미지 업로드를 위한 URL을 생성합니다. 생성된 URL에 PUT으로 이미지를 업로드 한 뒤 key를 파티 생성시에 첨부할 수 있습니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "OK", description = "파티 이미지 URL 생성 성공")
    })
    public ResponseEntity<ResponseData<FileUploadResponse>> createPartyImageUrl() {
        return ResponseData.toResponseEntity(ResponseCode.IMAGE_URL_GENERATE_SUCCESS, partyService.generateImageUploadUrl());
    }

    @PostMapping("")
    @Operation(summary = "파티 생성", description = "파티를 생성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "CREATED", description = "파티 생성 성공"),
        @ApiResponse(responseCode = "BAD_REQUEST", description = "파티 생성 실패")
    })
    public ResponseEntity<ResponseData> createParty(@CurrentUser Account user, @RequestBody @Valid PartyCreateRequest partyCreateRequest) {
        return ResponseData.toResponseEntity(partyService.createParty(user, partyCreateRequest));
    }
}
