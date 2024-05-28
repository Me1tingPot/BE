package meltingpot.server.post.dto;

import lombok.*;
import meltingpot.server.domain.entity.enums.PostType;

import java.time.LocalDateTime;
import java.util.List;

public class PostResponseDTO {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CreatePostResultDTO {
        private Long postId;
        private LocalDateTime createAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PostsListDTO {
        private Long postId;
        private String name;
        private String title;
        private String content;
        private Integer commentCount;
        private LocalDateTime updatedAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PageDTO{
        private List< PostsListDTO> pageDtos;
        private Long nextCursor;
        private Boolean isLast;
    }


}
