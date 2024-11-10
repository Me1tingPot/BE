package meltingpot.server.post.controller;

import lombok.RequiredArgsConstructor;
import java.util.NoSuchElementException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import meltingpot.server.post.dto.PostDetailResponse;
import meltingpot.server.post.dto.PostsListResponse;
import meltingpot.server.util.r2.FileService;
import meltingpot.server.util.r2.FileUploadResponse;
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
    private final FileService fileService;

    @Operation(summary = "이미지 업로드를 위한 Pre-signed URL 생성")
    @PostMapping("/upload-url")
    public ResponseEntity<FileUploadResponse> getUploadUrl(@RequestParam String prefix) {
        FileUploadResponse response = fileService.getPreSignedUrl(prefix);
        return ResponseEntity.ok(response);
    }
    @Operation(summary = "게시물 작성", description="requestParam으로 임시저장 여부를 알려주세요." +
                                                    "이미지가 없을 때는 빈 값으로 주시면 됩니다.")
    @PostMapping("")
    public ResponseEntity<ResponseData> createPost( @CurrentUser Account account,
                                                    @RequestBody PostCreateRequest postCreateRequest,
                                                    @RequestParam boolean isDraft) {
        try{
            return ResponseData.toResponseEntity(postService.createPost(postCreateRequest,account,isDraft));
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
    public ResponseEntity<ResponseData<PostDetailResponse>> getPostDetail(@CurrentUser Account account, @PathVariable Long postId) {
        try{
            return ResponseData.toResponseEntity(ResponseCode.POST_DETAIL_FETCH_SUCCEESS,postService.getPostDetail(postId));
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

    @GetMapping("/temp-saved")
    @Operation(summary = "임시저장된 글 불러오기", description = "임시저장된 글 불러오기")
    public ResponseEntity<ResponseData<PostDetailResponse>> getTempPost(@CurrentUser Account account) {
        try {
            return ResponseData.toResponseEntity(ResponseCode.POST_DETAIL_FETCH_SUCCEESS,postService.getTempPost(account));
        } catch (NoSuchElementException e) {
            return ResponseData.toResponseEntity(ResponseCode.POST_NOT_FOUND,null);
        }
    }

}
