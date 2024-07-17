package meltingpot.server.post.converter;

import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.post.Post;
import meltingpot.server.domain.entity.post.PostImage;
import meltingpot.server.post.dto.PostRequestDTO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class PostImageConverter {
    public static List<PostImage> toPostImage(PostRequestDTO.CreatePostDTO createPostDTO, Account account,Post post){
        if (createPostDTO.getImageKeys() == null || createPostDTO.getImageKeys().isEmpty()) {
            return Collections.emptyList();
        }
        return createPostDTO.getImageKeys().stream()
                .map(imageKey->PostImage.builder()
                        .imageKey(imageKey)
                        .post(post)
                        .account(account)
                        .build())
                .collect(Collectors.toList());
    }

}
