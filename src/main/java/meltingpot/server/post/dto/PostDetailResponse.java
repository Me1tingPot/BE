package meltingpot.server.post.dto;

import lombok.*;
import meltingpot.server.comment.dto.CommentResponseDTO;
import meltingpot.server.comment.dto.CommentsListResponse;
import meltingpot.server.domain.entity.post.Post;
import meltingpot.server.domain.entity.post.PostImage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDetailResponse {
    private Long postId;
    private String name;
    private String title;
    private String content;
    List<String> imgUrls;
    private Integer commentCount;
    private CommentsListResponse commentsList;
    private LocalDateTime updatedAt;

    public static PostDetailResponse  of (Post post, CommentsListResponse commentsList) {
        List<String> imgUrls = post.getPostImages().stream()
                .map(PostImage::getImageUrl)
                .collect(Collectors.toList());
        int commentCount = post.getComments().stream()
                .mapToInt(parentComment -> 1 + parentComment.getChildren().size())
                .sum();
        return PostDetailResponse.builder()
                .postId(post.getId())
                .name(post.getAccount().getName())
                .title(post.getTitle())
                .content(post.getContent())
                .imgUrls(imgUrls)
                .commentCount(commentCount)
                .commentsList(commentsList)
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
