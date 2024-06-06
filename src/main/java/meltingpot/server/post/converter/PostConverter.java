package meltingpot.server.post.converter;

import meltingpot.server.comment.dto.CommentResponseDTO;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.post.Post;
import meltingpot.server.post.dto.PostImageDTO;
import meltingpot.server.post.dto.PostRequestDTO;
import meltingpot.server.post.dto.PostResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PostConverter {

    /*post 작성*/
    public static Post toPost(PostRequestDTO.CreatePostDTO createPostDTO, Account account) {
        return Post.builder()
                .title(createPostDTO.getTitle())
                .content(createPostDTO.getContent())
                .postType(createPostDTO.getPostType())
                .account(account)
                .build();
    }
    /*post 작성 응답*/
    public static PostResponseDTO.CreatePostResultDTO toCreatePostResult(List<String> urls,Post post){
        return PostResponseDTO.CreatePostResultDTO.builder()
                .postId(post.getId())
                .postImageUrls(urls)
                .build();
    }

//    public static List<PostImageDTO> toPostImageDto(Post post) {
//        return post.getPostImages().stream()
//                .map(p -> PostImageDTO.builder()
//                        .id(p.getId())
//                        .imgUrl(p.getImageUrl())
//                        .build())
//                .collect(Collectors.toList());
//    }

    /*post 목록 조회*/
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

//    public static PostResponseDTO.PostDetailDTO toPostDetailDTO(Post post, CommentResponseDTO.CommentsListDTO comments) {
//        List<CommentResponseDTO.CommentDetailDTO> allComments = comments.getParentComments().stream()
//                .flatMap(parentCommentDTO -> Stream.concat(
//                        Stream.of(parentCommentDTO.getParentComment()),
//                        parentCommentDTO.getChildrenComments().stream()
//                                .map(CommentResponseDTO.ParentCommentDTO::getChildrenComments)
//                ))
//                .collect(Collectors.toList());
//
//        return PostResponseDTO.PostDetailDTO.builder()
//                .postId(post.getId())
//                .name(post.getAccount().getName())
//                .title(post.getTitle())
//                .content(post.getContent())
//                .comments(allComments)
//                .updatedAt(post.getUpdatedAt())
//                .build();
//    }

}
