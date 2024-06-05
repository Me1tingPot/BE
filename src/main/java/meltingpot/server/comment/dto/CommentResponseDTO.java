package meltingpot.server.comment.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class CommentResponseDTO {

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CreateCommentResultDTO{
        private Long commentId;
        private List <String> commentImageUrls;
    }


    @Builder
    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CommentDetailDTO {
        private Long commentId;
        private String content;
        private String name;
        private Boolean isAnonymous;
        private String imageUrl;
        private LocalDateTime updatedAt;
    }


    @Builder
    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ParentCommentDTO{
        private CommentDetailDTO parentComment;
        private List<CommentDetailDTO> childrenComments;
    }

    @Builder
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CommentsListDTO{
        private List<ParentCommentDTO> parentComments;
        private Long nextCursor;
        private Boolean isLast;
    }

}
