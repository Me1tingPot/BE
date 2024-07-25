package meltingpot.server.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import meltingpot.server.domain.entity.comment.Comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentsListResponse {
    private List<CommentDetail> commentsList;
    private Long nextCursor;
    private Boolean isLast;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CommentDetail {
        private Long commentId;
        private Long parentId;
        private Long userId;
        private String content;
        private String name;
        private Boolean isAnonymous;
        private String imageUrl;
        private LocalDateTime updatedAt;

        public static CommentDetail from(Comment comment) {
            return CommentDetail.builder()
                    .commentId(comment.getId())
                    .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                    .userId(comment.getAccount().getId())
                    .content(comment.getContent())
                    .name(comment.getAccount().getName())
                    .isAnonymous(comment.getIsAnonymous())
                    .imageUrl(comment.getCommentImage() != null ? comment.getCommentImage().getImageUrl() : null)
                    .updatedAt(comment.getUpdatedAt())
                    .build();
        }
    }

    public static CommentsListResponse from(List<CommentDetail> commentDetails, Long nextCursor, Boolean isLast) {
        return CommentsListResponse.builder()
                .commentsList(commentDetails)
                .nextCursor(nextCursor)
                .isLast(isLast)
                .build();
    }
}
