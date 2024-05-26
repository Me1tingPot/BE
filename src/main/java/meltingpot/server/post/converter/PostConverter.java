package meltingpot.server.post.converter;

import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.Post;
import meltingpot.server.post.dto.PostImageDTO;
import meltingpot.server.post.dto.PostRequestDTO;

import java.util.List;
import java.util.stream.Collectors;

public class PostConverter {
    public static Post toPost(PostRequestDTO.CreatePostDTO createPostDTO, Account account) {

        return Post.builder()
                .title(createPostDTO.getTitle())
                .content(createPostDTO.getContent())
                .postType(createPostDTO.getPostType())
                .account(account)
                .build();
    }

    public static List<PostImageDTO> toPostImageDto(Post post) {
        return post.getPostImages().stream()
                .map(p -> PostImageDTO.builder()
                        .id(p.getId())
                        .imgUrl(p.getImageUrl())
                        .build())
                .collect(Collectors.toList());
    }
}
