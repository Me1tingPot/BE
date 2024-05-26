package meltingpot.server.comment.service;

import meltingpot.server.comment.dto.CommentRequestDTO;
import meltingpot.server.post.dto.PostRequestDTO;

public interface CommentService {
    void createComment (CommentRequestDTO.CreateCommentDTO createCommentDTO,Long accountId,Long postId);
}
