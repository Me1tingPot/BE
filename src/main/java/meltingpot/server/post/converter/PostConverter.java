package meltingpot.server.post.converter;

import meltingpot.server.domain.entity.Post;
import meltingpot.server.post.dto.PostImageDto;
import meltingpot.server.post.dto.PostRequestDto;

import java.util.List;
import java.util.stream.Collectors;

public class PostConverter {
    public static Post toPost(PostRequestDto.CreatePostDTO createPostDTO) {

        return Post.builder()
                .title(createPostDTO.getTitle())
                .content(createPostDTO.getContent())
                .postType(createPostDTO.getPostType())
                .build();
    }

    public static List<PostImageDto> toPostImageDto(Post post) {
        return post.getPostImages().stream()
                .map(p -> PostImageDto.builder()
                        .id(p.getId())
                        .imgUrl(p.getImageUrl())
                        .build())
                .collect(Collectors.toList());
    }
}
