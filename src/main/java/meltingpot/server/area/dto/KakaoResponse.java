package meltingpot.server.area.dto;

import java.util.List;

public record KakaoResponse(
        KakaoMetaResponse meta,
        List<KakaoGeocodingDocumentResponse> documents
) {
}
