package meltingpot.server.area.dto;

public record KakaoGeocodingDocumentResponse(
        String region_type,
        String address_name,
        String region_1depth_name,
        String region_2depth_name,
        String region_3depth_name,
        String region_4depth_name,
        String code,
        Double x,
        Double y
) {

}
