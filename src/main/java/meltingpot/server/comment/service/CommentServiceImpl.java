package meltingpot.server.comment.service;

import lombok.RequiredArgsConstructor;
import meltingpot.server.comment.converter.CommentConverter;
import meltingpot.server.comment.dto.CommentRequestDTO;
import meltingpot.server.comment.dto.CommentResponseDTO;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.comment.Comment;
import meltingpot.server.domain.entity.comment.CommentImage;
import meltingpot.server.domain.entity.post.Post;
import meltingpot.server.domain.repository.AccountRepository;
import meltingpot.server.domain.repository.CommentImageRepository;
import meltingpot.server.domain.repository.CommentRepository;
import meltingpot.server.domain.repository.PostRepository;
import meltingpot.server.util.r2.FileService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static meltingpot.server.comment.converter.CommentConverter.*;
import static meltingpot.server.comment.converter.CommentImageConverter.toCommentImage;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final AccountRepository accountRepository;
    private final PostRepository postRepository;
    private final CommentImageRepository commentImageRepository;

    @Autowired
    private FileService fileService;


    @Override
    public CommentResponseDTO.CreateCommentResultDTO createComment(CommentRequestDTO.CreateCommentDTO createCommentDTO, Account account, Long postId) {
        Post post = findPostById(postId);
        Comment comment = toComment(createCommentDTO, account, post);
        return processCommentCreation(createCommentDTO, account, comment);
    }

    @Override
    public CommentResponseDTO.CreateCommentResultDTO createChildComment(CommentRequestDTO.CreateCommentDTO createCommentDTO, Account account, Long commentId) {
        Comment parentComment = findCommentById(commentId);
        Comment childComment = CommentConverter.toChildComment(createCommentDTO, account, parentComment);
        return processCommentCreation(createCommentDTO, account, childComment);
    }

    @Override
    public CommentResponseDTO.CreateCommentResultDTO updateComment(CommentRequestDTO.CreateCommentDTO updateCommentDTO, Account account, Long commentId) {
        // 댓글을 ID로 찾기
        Comment comment = findCommentById(commentId);

        // 댓글 내용 업데이트
        comment.setContent(updateCommentDTO.getContent());

        // 새로운 이미지 키와 기존 댓글 이미지 가져오기
        String newImageKey = updateCommentDTO.getImageKey();
        CommentImage oldCommentImage = comment.getCommentImage();

        if (newImageKey == null || newImageKey.isEmpty()) {
            // 새로운 이미지 키가 없을 경우 기존 이미지 삭제
            if (oldCommentImage != null) {
                commentImageRepository.delete(oldCommentImage);
                comment.setCommentImage(null);
            }
        } else {
            // 새로운 이미지 키가 있을 경우 이미지 업데이트 또는 생성
            if (oldCommentImage != null) {
                oldCommentImage.setImageKey(newImageKey);
                commentImageRepository.save(oldCommentImage);
            } else {
                CommentImage newCommentImage = toCommentImage(updateCommentDTO, account, comment);
                comment.setCommentImage(newCommentImage);
            }
        }


        // 댓글 저장
        commentRepository.save(comment);

        // 결과 DTO 생성 및 반환
        String commentImgUrl = newImageKey != null && !newImageKey.isEmpty() ? fileService.getCdnUrl("comment", newImageKey) : null;
        return toCreateCommentResult(commentImgUrl, comment);
    }

    public CommentResponseDTO.CommentsListDTO getCommentsList(Account account, Long postId, Long cursor, int pageSize) {
        List<CommentResponseDTO.CommentDetailDTO> commentDetailDTOs = new ArrayList<>();
        int count = 0;
        Long parentCursor = null;

        // Cursor가 자식 댓글에 해당하는 경우 처리
        if (cursor != null) {
            // Cursor가 부모 댓글이 아닌 자식 댓글을 나타내는 경우
            Comment childComment = commentRepository.findById(cursor).orElse(null);
            if (childComment != null && childComment.getParent() != null) {
                Comment parentComment = childComment.getParent();
                List<Comment> remainingChildren = commentRepository.findChildrenCommentsByParentId(parentComment.getId(), cursor);
                for (Comment child : remainingChildren) {
                    if (count >= pageSize) break;
                    commentDetailDTOs.add(toCommentDetailDTO(child));
                    count++;
                }
                parentCursor = parentComment.getId();
            }
        }

        // 부모 댓글과 자식 댓글을 가져오는 처리
        if (count < pageSize) {
            Pageable pageable = PageRequest.of(0, pageSize - count);
            List<Comment> parentComments = commentRepository.findParentCommentsByPostId(postId, parentCursor, pageable);
            for (Comment parent : parentComments) {
                if (count >= pageSize) break;
                commentDetailDTOs.add(toCommentDetailDTO(parent));
                count++;

                List<Comment> children = commentRepository.findChildrenCommentsByParentId(parent.getId(),null);
                for (Comment child : children) {
                    if (count >= pageSize) break;
                    commentDetailDTOs.add(toCommentDetailDTO(child));
                    count++;
                }
            }
        }

        Long nextCursor = (count < pageSize) ? null : commentDetailDTOs.get(commentDetailDTOs.size() - 1).getCommentId();
        boolean isLast = (count < pageSize);

        return toCommentsListDTO(commentDetailDTOs,nextCursor,isLast);
    }







    private CommentResponseDTO.CreateCommentResultDTO processCommentCreation(CommentRequestDTO.CreateCommentDTO createCommentDTO, Account account, Comment comment) {
        String commentImgUrl = null;
        if (createCommentDTO.getImageKey() != null && !createCommentDTO.getImageKey().isEmpty()) {
            String prefix = "comment";
            commentImgUrl = fileService.getCdnUrl(prefix, createCommentDTO.getImageKey());
        }
        CommentImage commentImage = toCommentImage(createCommentDTO, account, comment);
        comment.setCommentImage(commentImage);
        commentRepository.save(comment);

        return toCreateCommentResult(commentImgUrl, comment);
    }

    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
    }


    private Account findAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시물을 찾을 수 없습니다."));
    }

}



