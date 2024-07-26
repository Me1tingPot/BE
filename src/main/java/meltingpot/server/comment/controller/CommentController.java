package meltingpot.server.comment.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import meltingpot.server.comment.dto.CommentCreateRequest;
import meltingpot.server.comment.dto.CommentsListResponse;
import meltingpot.server.comment.service.CommentService;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.util.CurrentUser;
import meltingpot.server.util.ResponseCode;
import meltingpot.server.util.ResponseData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {
    private final CommentService commentService;

    @Operation(summary = "댓글 작성, 이미지가 없으면 null로 주시면 됩니다. ")
    @PostMapping("/{postId}")
    public ResponseEntity<ResponseData> createComment(@RequestBody CommentCreateRequest commentCreateRequest, @CurrentUser Account account, @PathVariable Long postId) {
        try {
            return ResponseData.toResponseEntity(commentService.createComment(commentCreateRequest,account,postId));
        } catch (NoSuchElementException e) {
            return ResponseData.toResponseEntity(ResponseCode.COMMENT_CREATE_FAIL);
        }
    }

    @Operation(summary = "대댓글 작성, 이미지가 없으면 null로 주시면 됩니다.")
    @PostMapping("/child/{commentId}")
    public ResponseEntity<ResponseData> createChildComment(@RequestBody CommentCreateRequest commentCreateRequest, @CurrentUser Account account,@PathVariable Long commentId) {
        try {
            return ResponseData.toResponseEntity(commentService.createChildComment(commentCreateRequest,account,commentId));
        } catch (NoSuchElementException e) {
            return ResponseData.toResponseEntity(ResponseCode.COMMENT_CREATE_FAIL);
        }
    }

    @Operation(summary = "댓글 수정, 이미지가 없으면 null로 주시면 됩니다.")
    @PutMapping("/{commentId}")
    public ResponseEntity<ResponseData> updateComment(@RequestBody CommentCreateRequest commentCreateRequest, @CurrentUser Account account, @PathVariable Long commentId){
        try {
            return ResponseData.toResponseEntity(commentService.updateComment(commentCreateRequest,account,commentId));
        } catch (NoSuchElementException e) {
            return ResponseData.toResponseEntity(ResponseCode.COMMENT_UPDATE_FAIL);
        }
    }

    @Operation(summary = "댓글 목록 가져오기")
    @GetMapping("/list/{postId}")
    public ResponseEntity<ResponseData<CommentsListResponse>> getCommentsList (@CurrentUser Account account, @PathVariable Long postId,
                                                                               @RequestParam(required = false) Long cursor,
                                                                               @RequestParam(defaultValue = "10") int pageSize){
        try{
            return ResponseData.toResponseEntity(ResponseCode.READ_COMMENTS_LIST_SUCCESS, commentService.getCommentsList(account,postId,cursor,pageSize));
        }catch (NoSuchElementException e) {
            return ResponseData.toResponseEntity(ResponseCode.READ_COMMENT_FAIL, null);
        }
    }
}


