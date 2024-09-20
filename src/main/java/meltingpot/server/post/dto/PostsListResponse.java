package meltingpot.server.post.dto;


import lombok.*;
import meltingpot.server.domain.entity.post.Post;
import meltingpot.server.domain.entity.post.PostImage;
import meltingpot.server.domain.entity.AccountProfileImage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostsListResponse {
    private List<PostsList> postsList;
    private Long nextCursor;
    private Boolean isLast;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PostsList {
        private Long postId;
        private Long userId;
        private String profileImg;
        private String name;
        private String title;
        private String content;
        List<String> imgUrls;
        private Integer commentCount;
        private LocalDateTime updatedAt;

        public static PostsList from(Post post) {
            List<String> imgUrls = post.getPostImages().stream()
                    .map(PostImage::getImageUrl)
                    .collect(Collectors.toList());
            String profileImgKey = post.getAccount().getProfileImages().stream()
                    .filter(AccountProfileImage::isThumbnail)
                    .map(AccountProfileImage::getImageKey)
                    .findFirst()
                    .orElse(null);
            return PostsList.builder()
                    .postId(post.getId())
                    .userId(post.getAccount().getId())
                    .profileImg(profileImgKey)
                    .name(post.getAccount().getName())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .commentCount(post.getComments().size())
                    .imgUrls(imgUrls)
                    .updatedAt(post.getUpdatedAt())
                    .build();
        }
    }

    public static PostsListResponse from(List<Post> posts,Long nextCursor,Boolean isLast) {
        List<PostsList> postsList = posts.stream()
                .map(PostsList::from)
                .collect(Collectors.toList());
        return PostsListResponse.builder()
                .postsList(postsList)
                .nextCursor(nextCursor)
                .isLast(isLast)
                .build();
    }



}
