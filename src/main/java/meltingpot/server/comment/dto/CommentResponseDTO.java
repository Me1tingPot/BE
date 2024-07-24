package meltingpot.server.comment.dto;

import lombok.*;
import meltingpot.server.domain.entity.comment.Comment;

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
        private String commentImageUrl;
    }


    @Builder
    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CommentDetailDTO {
        private Long commentId;
        private Long parentId;
        private Long userId;
        private String content;
        private String name;
        private Boolean isAnonymous;
        private String imageUrl;
        private LocalDateTime updatedAt;
        private List<CommentDetailDTO> children;
    }



    @Builder
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CommentsListDTO{
        private List<CommentDetailDTO> comments;
        private Long nextCursor;
        private Boolean isLast;
    }

    public  class CommentQueueItem {
        public Comment parent;
        public List<Comment> children;

        public CommentQueueItem(Comment parent, List<Comment> children) {
            this.parent = parent;
            this.children = children;
        }
    }

}
