package meltingpot.server.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class PostResponseDto {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CreatePostResultDTO {
        private Long postId;
        private LocalDateTime createAt;
    }
}
