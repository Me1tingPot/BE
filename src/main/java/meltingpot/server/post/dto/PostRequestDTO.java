package meltingpot.server.post.dto;

import lombok.Getter;
import meltingpot.server.domain.entity.enums.PostType;

public class PostRequestDTO {

    @Getter
    public static class CreatePostDTO {
        private String title;
        private String content;
        private PostType postType;
//        private List<PostImage> postImageList;
    }
}
