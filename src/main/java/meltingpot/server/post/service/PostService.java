package meltingpot.server.post.service;

import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.enums.PostType;
import meltingpot.server.post.dto.PostRequestDTO;
import meltingpot.server.post.dto.PostResponseDTO;

import org.springframework.data.domain.Pageable;

public interface PostService {

    void createPost (PostRequestDTO.CreatePostDTO createPostDTO, Account account);

    PostResponseDTO.PageDTO getPostsList(PostType postType, Account account,Long cursor, Pageable pageable);


}







