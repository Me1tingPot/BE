package meltingpot.server.area.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import meltingpot.server.area.dto.AreaResponse;
import meltingpot.server.area.service.AreaService;
import meltingpot.server.util.ResponseCode;
import meltingpot.server.util.ResponseData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/area")
@RequiredArgsConstructor
public class AreaController {
    private final AreaService areaService;

    @GetMapping("/")
    @Operation(summary = "최상위 지역 조회", description = "최상위 지역을 조회합니다. 해당 API를 호출 후 하위 지역을 조회할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "OK", description = "지역 조회 성공")
    })
    public ResponseEntity<ResponseData<List<AreaResponse>>> listArea() {
        return ResponseData.toResponseEntity(ResponseCode.AREA_FETCH_SUCCESS, areaService.listArea(null));
    }

    @GetMapping("/{parentAreaId}")
    @Operation(summary = "하위 지역 조회", description = "하위 지역을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "OK", description = "지역 조회 성공")
    })
    public ResponseEntity<ResponseData<List<AreaResponse>>> listChildArea(@PathVariable("parentAreaId") Integer parentAreaId) {
        return ResponseData.toResponseEntity(ResponseCode.AREA_FETCH_SUCCESS, areaService.listArea(parentAreaId));
    }
}
