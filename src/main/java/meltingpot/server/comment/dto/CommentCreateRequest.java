package meltingpot.server.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.comment.Comment;
import meltingpot.server.domain.entity.post.Post;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateRequest {
    private String content;
    private Boolean isAnonymous;
    private String  imageKey;

    public Comment toEntity(Post post, Account account, Comment parentComment){
        return Comment.builder()
                .content(content)
                .isAnonymous(isAnonymous)
                .post(post)
                .account(account)
                .parent(parentComment)
                .build();
    }
}
