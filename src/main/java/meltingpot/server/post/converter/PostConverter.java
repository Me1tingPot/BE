package meltingpot.server.post.converter;

import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.Post;
import meltingpot.server.post.dto.PostImageDTO;
import meltingpot.server.post.dto.PostRequestDTO;
import meltingpot.server.post.dto.PostResponseDTO;
import org.springframework.data.domain.Page;

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


    public  static PostResponseDTO.PostsListDTO toPostsListDTO(Post post) {
        return PostResponseDTO.PostsListDTO.builder()
                .postId(post.getId())
                .name(post.getAccount().getName()) // Assuming Account entity has getName method
                .title(post.getTitle())
                .content(post.getContent())
                .commentCount(post.getComments().size())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    public static PostResponseDTO.PageDTO toPageDTO(Page<Post>posts) {
        List<PostResponseDTO.PostsListDTO> postsListDTOS = null;
        Long nextCursor = null;

        if (!posts.getContent().isEmpty()) {
            postsListDTOS = posts.stream().map((Post post) -> toPostsListDTO(post)).toList();
            nextCursor = posts.getContent().get(posts.getContent().size() - 1).getId();
        }
        return PostResponseDTO.PageDTO.builder()
                .pageDtos(postsListDTOS)
                .nextCursor(nextCursor)
                .isLast(posts.isLast())
                .build();
    }
}
