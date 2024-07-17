package meltingpot.server.comment.converter;

import lombok.RequiredArgsConstructor;
import meltingpot.server.comment.dto.CommentRequestDTO;
import meltingpot.server.comment.dto.CommentResponseDTO;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.comment.Comment;
import meltingpot.server.domain.entity.post.Post;

import java.util.List;

@RequiredArgsConstructor
public class CommentConverter {
    public static Comment toComment(CommentRequestDTO.CreateCommentDTO createCommentDTO, Account account, Post post) {
        return Comment.builder()
                .content(createCommentDTO.getContent())
                .isAnonymous(createCommentDTO.getIsAnonymous())
                .post(post)
                .account(account)
                .build();
    }

    public static Comment toChildComment(CommentRequestDTO.CreateCommentDTO createCommentDTO, Account account,Comment parentComment){
        return  Comment.builder()
                .content(createCommentDTO.getContent())
                .isAnonymous(createCommentDTO.getIsAnonymous())
                .parent(parentComment)
                .account(account)
                .build();
    }
    public static CommentResponseDTO.CreateCommentResultDTO toCreateCommentResult(String url,Comment comment){
        return CommentResponseDTO.CreateCommentResultDTO.builder()
                .commentId(comment.getId())
                .commentImageUrl(url)
                .build();
    }

    public static CommentResponseDTO.CommentDetailDTO toCommentDetailDTO(Comment comment){
        return CommentResponseDTO.CommentDetailDTO.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .name(comment.getAccount().getName())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    public static CommentResponseDTO.ParentCommentDTO toParentCommentDTO(Comment parentComment,List<CommentResponseDTO.CommentDetailDTO>childrenComments){
        return CommentResponseDTO.ParentCommentDTO.builder()
                .parentComment(toCommentDetailDTO(parentComment))
                .childrenComments(childrenComments)
                .build();
    }

    public static CommentResponseDTO.CommentsListDTO toCommentsListDTO(List<CommentResponseDTO.ParentCommentDTO> parentComments, Long nextCursor, Boolean isLast){
        return CommentResponseDTO.CommentsListDTO.builder()
                .parentComments(parentComments)
                .nextCursor(nextCursor)
                .isLast(isLast)
                .build();
    }
}