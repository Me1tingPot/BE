package meltingpot.server.util.r2;

import lombok.Builder;

@Builder
public record FileUploadResponse(
    String uploadUrl,
    String fileKey
) {
}
