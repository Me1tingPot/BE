package meltingpot.server.post.service;

import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.repository.AccountRepositroy;
import meltingpot.server.post.dto.PostRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {

    void createPost (PostRequestDto.CreatePostDTO createPostDTO);
    }



