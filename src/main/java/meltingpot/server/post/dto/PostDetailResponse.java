package meltingpot.server.post.dto;

import lombok.*;
import meltingpot.server.comment.dto.CommentsListResponse;
import meltingpot.server.domain.entity.post.Post;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDetailResponse {
    private Long postId;
    private Long userId;
    private String name;
    private String title;
    private String content;
    private List<ImageData> imgData;  // Image data including ID and URL
    private Integer commentCount;
    private CommentsListResponse commentsList;
    private LocalDateTime updatedAt;

    // Static nested class to hold image data
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ImageData {
        private Long imageId;
        private String imageUrl;
    }

    public static PostDetailResponse of(Post post, CommentsListResponse commentsList) {
        List<ImageData> imgData = post.getPostImages().stream()
                .map(postImage -> new ImageData(postImage.getId(), postImage.getImageUrl()))
                .collect(Collectors.toList());

        int commentCount = post.getComments().stream()
                .mapToInt(parentComment -> 1 + parentComment.getChildren().size())
                .sum();

        return PostDetailResponse.builder()
                .postId(post.getId())
                .userId(post.getAccount().getId())
                .name(post.getAccount().getName())
                .title(post.getTitle())
                .content(post.getContent())
                .imgData(imgData)
                .commentCount(commentCount)
                .commentsList(commentsList)
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    public static PostDetailResponse from(Post post) {
        List<ImageData> imgData = post.getPostImages().stream()
                .map(postImage -> new ImageData(postImage.getId(), postImage.getImageUrl()))
                .collect(Collectors.toList());

        return PostDetailResponse.builder()
                .postId(post.getId())
                .name(post.getAccount().getName())
                .title(post.getTitle())
                .content(post.getContent())
                .imgData(imgData)
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
