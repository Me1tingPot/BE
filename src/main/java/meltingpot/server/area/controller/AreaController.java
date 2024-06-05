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

    @GetMapping("/{areaId}/parent")
    @Operation(summary = "상위 지역 조회", description = "상위 지역을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "OK", description = "지역 조회 성공")
    })
    public ResponseEntity<ResponseData<List<AreaResponse>>> listParentAreas(@PathVariable("areaId") Integer areaId) {
        return ResponseData.toResponseEntity(ResponseCode.AREA_FETCH_SUCCESS, areaService.listParentAreas(areaId));
    }

    @GetMapping("/search-by-coord")
    @Operation(summary = "좌표로 지역 조회", description = "좌표로 지역을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "OK", description = "지역 조회 성공")
    })
    public ResponseEntity<ResponseData<AreaResponse>> getAreaByCoordinate(Double latitude, Double longitude) {
        try {
            return ResponseData.toResponseEntity(ResponseCode.AREA_FETCH_SUCCESS, areaService.getAreaByCoordinate(latitude, longitude));
        } catch (Exception e) {
            if (e.getMessage().equals("해당 좌표는 한국 내에 존재하지 않습니다.")) {
                return ResponseData.toResponseEntity(ResponseCode.AREA_FETCH_FAILED_NOT_SERVICE_AREA, null);
            } else if (e.getMessage().equals("해당 좌표와 일치하는 결과를 찾을 수 없었습니다.")) {
                return ResponseData.toResponseEntity(ResponseCode.AREA_FETCH_FAILED_NOT_IN_OUR_DB, null);
            } else if (e.getMessage().equals("법정동 정보가 없는 좌표입니다.")) {
                return ResponseData.toResponseEntity(ResponseCode.AREA_FETCH_FAILED_NO_BDONG_INFO, null);
            } else if (e.getMessage().equals("해당 지역 정보가 없습니다.")) {
                return ResponseData.toResponseEntity(ResponseCode.AREA_FETCH_FAILED_NOT_IN_OUR_DB, null);
            }
            return ResponseData.toResponseEntity(ResponseCode.AREA_FETCH_FAILED, null);
        }
    }
}
