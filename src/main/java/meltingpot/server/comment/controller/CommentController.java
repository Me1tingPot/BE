package meltingpot.server.comment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import meltingpot.server.comment.dto.CommentRequestDTO;
import meltingpot.server.comment.service.CommentService;
import meltingpot.server.post.dto.PostRequestDTO;
import meltingpot.server.post.service.PostService;
import meltingpot.server.util.ResponseCode;
import meltingpot.server.util.ResponseData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {
    private final CommentService commentService;

    @Operation(summary = "댓글 작성")
    @PostMapping("/{postId}")
    public ResponseEntity<ResponseData> createComment(@RequestBody CommentRequestDTO.CreateCommentDTO createCommentDTO,@RequestParam Long accountId, @PathVariable Long postId) {
        commentService.createComment(createCommentDTO,accountId,postId);
        return ResponseData.toResponseEntity(ResponseCode.CREATE_COMMENT_SUCCESS);
    }
}


