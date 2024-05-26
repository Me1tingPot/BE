package meltingpot.server.post.converter;

import meltingpot.server.domain.entity.Post;
import meltingpot.server.domain.entity.PostImage;
import org.springframework.web.multipart.MultipartFile;

public class PostImageConverter {
    public static PostImage toPostImage(Post post, MultipartFile file, String pictureUrl) {
        return PostImage.builder()
                .post(post)
                .imageUrl(pictureUrl)
                .build();
    }
}
