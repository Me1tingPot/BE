package meltingpot.server.user.controller.dto;

import lombok.*;
import meltingpot.server.domain.entity.enums.PostType;
import meltingpot.server.domain.entity.post.Post;
import meltingpot.server.user.service.UserService;
import org.apache.catalina.User;

import java.time.LocalDateTime;

@Builder
@Getter
public class PostResponseDto {
    private Long postId;
    private String title;
    private String content;
    private PostType postType;
    private Integer commentCount;
    private LocalDateTime createdAt;
    private String profileImage;
    private String authorName;

    public static PostResponseDto of(Post post, String profileImage){
        return PostResponseDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .postType(post.getPostType())
                .commentCount(post.getComments().size())
                .createdAt(post.getCreatedAt())
                .authorName(post.getAccount().getName())
                .profileImage(profileImage)
                .build();
    }

}
