package meltingpot.server.comment.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import meltingpot.server.comment.dto.CommentRequestDTO;
import meltingpot.server.comment.dto.CommentResponseDTO;
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

    @Operation(summary = "댓글 작성")
    @PostMapping("/{postId}")
    public ResponseEntity<ResponseData<CommentResponseDTO.CreateCommentResultDTO>> createComment(@RequestBody CommentRequestDTO.CreateCommentDTO createCommentDTO, @CurrentUser Account account, @PathVariable Long postId) {
        try {
            return ResponseData.toResponseEntity(ResponseCode.CREATE_COMMENT_SUCCESS, commentService.createComment(createCommentDTO,account,postId));
        } catch (NoSuchElementException e) {
            return ResponseData.toResponseEntity(ResponseCode.COMMENT_CREATE_FAIL, null);
        }
    }

    @Operation(summary = "대댓글 작성")
    @PostMapping("/child/{commentId}")
    public ResponseEntity<ResponseData<CommentResponseDTO.CreateCommentResultDTO>> createChildComment(@RequestBody CommentRequestDTO.CreateCommentDTO createCommentDTO, @CurrentUser Account account,@PathVariable Long commentId) {
        try {
            return ResponseData.toResponseEntity(ResponseCode.CREATE_CHILD_COMMENT_SUCCESS, commentService.createChildComment(createCommentDTO,account,commentId));
        } catch (NoSuchElementException e) {
            return ResponseData.toResponseEntity(ResponseCode.COMMENT_CREATE_FAIL, null);
        }
    }

    @Operation(summary = "댓글 수정")
    @PutMapping("/{commentId}")
    public ResponseEntity<ResponseData<CommentResponseDTO.CreateCommentResultDTO>> updateComment(@RequestBody CommentRequestDTO.CreateCommentDTO createCommentDTO, @CurrentUser Account account, @PathVariable Long commentId){
        try {
            return ResponseData.toResponseEntity(ResponseCode.UPDATE_COMMENT_SUCCESS, commentService.updateComment(createCommentDTO,account,commentId));
        } catch (NoSuchElementException e) {
            return ResponseData.toResponseEntity(ResponseCode.COMMENT_UPDATE_FAIL, null);
        }
    }

}


