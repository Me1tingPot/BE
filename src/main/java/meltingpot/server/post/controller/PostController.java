package meltingpot.server.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import lombok.RequiredArgsConstructor;
import meltingpot.server.post.dto.PostRequestDto;
import meltingpot.server.post.dto.PostResponseDto;
import meltingpot.server.post.service.PostService;
import meltingpot.server.util.ResponseCode;
import meltingpot.server.util.ResponseData;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static meltingpot.server.util.ResponseCode.CREATE_POST_SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;

    @Operation(summary = "게시물 작성")
    @PostMapping("")
    public ResponseEntity<ResponseData> createPost( @RequestBody PostRequestDto.CreatePostDTO createPostDTO) {
        postService.createPost(createPostDTO);
        return ResponseData.toResponseEntity(ResponseCode.CREATE_POST_SUCCESS);
    }
}
