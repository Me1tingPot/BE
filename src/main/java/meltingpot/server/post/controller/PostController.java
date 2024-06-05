package meltingpot.server.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.enums.PostType;
import meltingpot.server.post.dto.PostRequestDTO;
import meltingpot.server.post.dto.PostResponseDTO;
import meltingpot.server.post.service.PostService;
import meltingpot.server.util.CurrentUser;
import meltingpot.server.util.ResponseCode;
import meltingpot.server.util.ResponseData;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import java.util.NoSuchElementException;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;

    @Operation(summary = "게시물 작성")
    @PostMapping("")
    public ResponseEntity<ResponseData<PostResponseDTO.CreatePostResultDTO>> createPost( @CurrentUser Account account,@RequestBody PostRequestDTO.CreatePostDTO createPostDTO) {
        postService.createPost(createPostDTO,account);
        try{
            return ResponseData.toResponseEntity(ResponseCode.CREATE_POST_SUCCESS, postService.createPost(createPostDTO,account));
        }catch (NoSuchElementException e) {
            return ResponseData.toResponseEntity(ResponseCode.POST_CREATE_FAIL,null);
        }
    }

    @GetMapping("/type/{postType}")
    @Operation(summary = "커뮤니티 글 목록 조회", description = "커뮤니티 글 목록을 조회합니다. type을 path variable로 받아 구분합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "OK", description = "커뮤니티 글 목록 조회 성공"),
            @ApiResponse(responseCode = "NOT_FOUND", description = "커뮤니티 글을 찾을 수 없습니다")
    })
    public ResponseEntity<ResponseData<PostResponseDTO.PageDTO>> getPostList(@PathVariable PostType postType, @CurrentUser Account account,  @RequestParam(name = "cursor") Long cursor, @RequestParam(name = "pageSize") Integer pageSize) {
        Pageable pageable = (Pageable) PageRequest.of(0, pageSize);
        try {
            return ResponseData.toResponseEntity(ResponseCode.POST_LIST_FETCH_SUCCESS, postService.getPostsList(postType,account,cursor, pageable));
        } catch (NoSuchElementException e) {
            return ResponseData.toResponseEntity(ResponseCode.POST_NOT_FOUND, null);
        }
    }


}
