package meltingpot.server.comment.dto;

import lombok.Getter;

public class CommentRequestDTO {

    @Getter
    public static class CreateCommentDTO{
        private String content;
    }
}
