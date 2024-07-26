package meltingpot.server.post.controller;

import lombok.RequiredArgsConstructor;
import java.util.NoSuchElementException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import meltingpot.server.domain.entity.comment.Comment;
import meltingpot.server.domain.entity.post.Post;
import meltingpot.server.domain.repository.CommentRepository;
import meltingpot.server.post.dto.PostDetailResponse;
import meltingpot.server.post.dto.PostsListResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import meltingpot.server.util.*;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.enums.PostType;
import meltingpot.server.post.dto.PostCreateRequest;
import meltingpot.server.post.service.PostService;




@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;

    @Operation(summary = "게시물 작성, 이미지가 없을 때는 빈 값으로 주시면 됩니다. ")
    @PostMapping("")
    public ResponseEntity<ResponseData> createPost( @CurrentUser Account account,@RequestBody PostCreateRequest createPostDTO) {
        try{
            return ResponseData.toResponseEntity(postService.createPost(createPostDTO,account));
        }catch (NoSuchElementException e) {
            return ResponseData.toResponseEntity(ResponseCode.POST_CREATE_FAIL);
        }
    }

    @Operation(summary = "게시물 수정, 이미지가 없을 때는 빈 값으로 주시면 됩니다. ")
    @PutMapping("/{postId}")
    public ResponseEntity<ResponseData> updatePost(@CurrentUser Account account,@PathVariable Long postId, @RequestBody PostCreateRequest updateRequest) {
        try {
            return ResponseData.toResponseEntity(postService.updatePost(updateRequest,postId, account));
        } catch (NoSuchElementException e) {
            return ResponseData.toResponseEntity(ResponseCode.POST_UPDATE_FAIL);
        }
    }


    @GetMapping("/type/{postType}")
    @Operation(summary = "커뮤니티 글 목록 조회", description = "커뮤니티 글 목록을 조회합니다. type을 path variable로 받아 구분합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "OK", description = "커뮤니티 글 목록 조회 성공"),
            @ApiResponse(responseCode = "NOT_FOUND", description = "커뮤니티 글을 찾을 수 없습니다")
    })
    public ResponseEntity<ResponseData<PostsListResponse>> getPostList(@CurrentUser Account account, @PathVariable PostType postType , @RequestParam(required = false, name = "cursor") Long cursor, @RequestParam(name = "pageSize") Integer pageSize) {
        try {
            return ResponseData.toResponseEntity(ResponseCode.POST_LIST_FETCH_SUCCESS, postService.getPostsList(account,postType, cursor, pageSize));
        } catch (NoSuchElementException e) {
            return ResponseData.toResponseEntity(ResponseCode.POST_NOT_FOUND, null);
        }
    }

    @GetMapping("/{postId}")
    @Operation(summary = "커뮤니티 글 내용 조회", description = "postId로 커뮤니티 글 내용을 조회합니다.")
    public ResponseEntity<ResponseData<PostDetailResponse>> getPostDetail(@CurrentUser Account account, @PathVariable Long postId, @RequestParam(required = false, name = "cursor") Long cursor, @RequestParam(name = "pageSize") Integer pageSize) {
        try{
            return ResponseData.toResponseEntity(ResponseCode.POST_DETAIL_FETCH_SUCCEESS,postService.getPostDetail(postId,cursor, pageSize));
        }catch (NoSuchElementException e) {
            return ResponseData.toResponseEntity(ResponseCode.POST_NOT_FOUND, null);
        }
    }


    @DeleteMapping("/{postId}")
    @Operation(summary = "커뮤니티 글 삭제", description = "postId로 커뮤니티 글 삭제")
    public ResponseEntity<ResponseData> deletePost(@CurrentUser Account account,@PathVariable Long postId) {
        try {
            return ResponseData.toResponseEntity(postService.deletePost(postId, account));
        } catch (NoSuchElementException e) {
            return ResponseData.toResponseEntity(ResponseCode.POST_DELETE_FAIL);
        }
    }

}
