package meltingpot.server.comment.converter;

import lombok.RequiredArgsConstructor;
import meltingpot.server.comment.dto.CommentRequestDTO;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.Comment;
import meltingpot.server.domain.entity.Post;

@RequiredArgsConstructor
public class CommentConverter {
    public static Comment toComment(CommentRequestDTO.CreateCommentDTO createCommentDTO, Account account, Post post) {
        return Comment.builder()
                .content(createCommentDTO.getContent())
                .account(account)
                .post(post)
                .build();
    }
}
