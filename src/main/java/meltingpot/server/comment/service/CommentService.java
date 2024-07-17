package meltingpot.server.comment.service;

import meltingpot.server.comment.dto.CommentRequestDTO;
import meltingpot.server.comment.dto.CommentResponseDTO;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.post.dto.PostRequestDTO;

public interface CommentService {
    CommentResponseDTO.CreateCommentResultDTO createComment (CommentRequestDTO.CreateCommentDTO createCommentDTO, Account account, Long postId);

    CommentResponseDTO.CreateCommentResultDTO createChildComment  (CommentRequestDTO.CreateCommentDTO createCommentDTO, Account account, Long commentId);

    CommentResponseDTO.CreateCommentResultDTO updateComment (CommentRequestDTO.CreateCommentDTO updateCommentDTO,Account account, Long commentId);
}
