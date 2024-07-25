package meltingpot.server.post.dto;


import lombok.*;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.enums.PostType;
import meltingpot.server.domain.entity.post.Post;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostCreateRequest {
    private String title;
    private String content;
    private PostType postType;
    private List<String> imageKeys;

    public Post toEntity(Account account){
        return Post.builder()
                .title(title)
                .content(content)
                .postType(postType)
                .account(account)
                .build();

    }
}
