package meltingpot.server.post.service;

import meltingpot.server.post.dto.PostRequestDTO;

public interface PostService {

    void createPost (PostRequestDTO.CreatePostDTO createPostDTO,Long accountId);
    }



