package meltingpot.server.area.service;

import lombok.RequiredArgsConstructor;
import meltingpot.server.area.dto.AreaResponse;
import meltingpot.server.area.dto.KakaoGeocodingDocumentResponse;
import meltingpot.server.area.dto.KakaoResponse;
import meltingpot.server.domain.entity.Area;
import meltingpot.server.domain.repository.AreaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AreaService {
    private final AreaRepository areaRepository;

    @Value("${cloud.kakao.rest_key}")
    private String kakaoRestKey;

    @Transactional
    public List<AreaResponse> listArea(String parentAreaId) {
        if (parentAreaId == null) {
            return areaRepository.findAllByAreaParentIdNull().stream()
                    .map(AreaResponse::of)
                    .collect(Collectors.toList());
        } else {
            return areaRepository.findAllByAreaParentId(parentAreaId).stream()
                    .map(AreaResponse::of)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    public List<AreaResponse> listParentAreas(String areaId) {
        Area area = areaRepository.findById(areaId).orElseThrow(() -> new IllegalArgumentException("해당 지역 정보가 없습니다."));
        List<AreaResponse> parentAreas = new ArrayList<>();

        parentAreas.add(AreaResponse.of(area));
        while (area.getAreaParent() != null) {
            area = areaRepository.findById(area.getAreaParent().getId()).orElseThrow(() -> new IllegalArgumentException("해당 지역 정보가 없습니다."));
            parentAreas.add(AreaResponse.of(area));
        }
        return parentAreas;
    }

    @Transactional
    public AreaResponse getAreaByCoordinate(Double latitude, Double longitude) {
        // 대략적인 국내 여부를 확인 (추후 다각형으로 전환 필요)
        if (latitude < 33.1 || latitude > 38.45 || longitude < 125.06 || longitude > 131.87) {
            throw new IllegalArgumentException("해당 좌표는 한국 내에 존재하지 않습니다.");
        }

        WebClient webClient = WebClient.builder().build();

        KakaoResponse response = webClient
                .get()
                .uri(String.format("https://dapi.kakao.com/v2/local/geo/coord2regioncode.json?x=%s&y=%s", longitude, latitude))
                .header("Authorization", String.format("KakaoAK %s", kakaoRestKey))
                .retrieve()
                .bodyToMono(KakaoResponse.class)
                .block();

        if (response == null || response.meta().total_count() == 0) {
            throw new IllegalArgumentException("해당 좌표와 일치하는 결과를 찾을 수 없었습니다.");
        }

        // 우리 시스템은 법정동 기반으로 운영된다
        KakaoGeocodingDocumentResponse bArea = response.documents().stream()
                .filter(document -> document.region_type().equals("B"))
                .findFirst()
                .orElse(null);

        if (bArea == null) {
            throw new IllegalArgumentException("법정동 정보가 없는 좌표입니다.");
        }

        List<String> areaNames = Arrays.asList(bArea.region_1depth_name(), bArea.region_2depth_name(), bArea.region_3depth_name(), bArea.region_4depth_name());
        Area lastArea = null;
        for (String areaName: areaNames) {
            if (areaName.isEmpty()) {
                continue;
            }

            Area area;
            if (lastArea == null) {
                area = areaRepository.findAreaByAreaParentIdIsNullAndAreaName(areaName);
            } else {
                area = areaRepository.findAreaByAreaParentIdAndAreaName(lastArea.getId(), areaName);
            }

            if (area == null) {
                // 만약 정보가 없는 경우 상위 지역을 반환한다
                continue;
            }
            lastArea = area;
        }

        if (lastArea == null) {
            throw new IllegalArgumentException("해당 지역 정보가 없습니다.");
        }

        return AreaResponse.of(lastArea);
    }
}
