package meltingpot.server.comment.dto;

import lombok.Getter;

import java.util.List;

public class CommentRequestDTO {

    @Getter
    public static class CreateCommentDTO{
        private String content;
        private Boolean isAnonymous;
        private List<String> imageKeys;
    }

    @Getter
    public class CreateChildCommentDTO {
        private String content;
        private boolean isAnonymous;
        private List<String> imageKey;
        private Long postId;
        private Long accountId;
        private Long parentId;

        // Getter와 Setter 생략
    }
}
