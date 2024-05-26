package meltingpot.server.post.dto;

import lombok.Getter;
import meltingpot.server.domain.entity.PostImage;
import meltingpot.server.domain.entity.enums.PostType;

import java.util.List;

public class PostRequestDto {

    @Getter
    public static class CreatePostDTO {
        private String title;
        private String content;
        private PostType postType;
//        private List<PostImage> postImageList;
    }
}
