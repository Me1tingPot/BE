package meltingpot.server.post.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import lombok.RequiredArgsConstructor;
import meltingpot.server.post.dto.PostRequestDto;
import meltingpot.server.post.service.PostService;
import meltingpot.server.util.ResponseCode;
import meltingpot.server.util.ResponseData;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static meltingpot.server.util.ResponseCode.CREATE_POST_SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;

    @PostMapping("/users/{userId}/posts")
    public ResponseEntity<ResponseData> createPost(@Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                                    @RequestPart(name = "postSaveDto")
                                    Long accountId,
                                     PostRequestDto.CreatePostDTO createPostDTO,
                                     @RequestPart(name = "images", required = false) List<MultipartFile> images) {
        postService.createPost(accountId,createPostDTO, images);
        return ResponseData.toResponseEntity(CREATE_POST_SUCCESS);
    }

}
