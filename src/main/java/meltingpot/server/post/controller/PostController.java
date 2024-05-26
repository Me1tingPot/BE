package meltingpot.server.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.post.dto.PostRequestDTO;
import meltingpot.server.post.service.PostService;
import meltingpot.server.util.ResponseCode;
import meltingpot.server.util.ResponseData;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;

    @Operation(summary = "게시물 작성")
    @PostMapping("")
    public ResponseEntity<ResponseData> createPost( @RequestBody PostRequestDTO.CreatePostDTO createPostDTO,@RequestParam Long accountId) {
        postService.createPost(createPostDTO,accountId);
        return ResponseData.toResponseEntity(ResponseCode.CREATE_POST_SUCCESS);
    }
}
