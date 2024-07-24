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
        // Retrieve the parent comment based on cursor
        Comment parentComment = getParentComment(cursor, postId);

        // If no parent comment found, throw an exception or handle accordingly
        if (parentComment == null) {
            throw new NoSuchElementException("No parent comment found for the given cursor.");
        }

        // Retrieve child comments for the parent comment
        List<Comment> childComments = getChildComments(parentComment, pageSize - 1); // -1 for the parent comment

        // Determine the next cursor and if it's the last page
        Long nextCursor = determineNextCursor(parentComment, childComments, pageSize);
        boolean isLast = nextCursor == null;

        // Convert parent comment to DTO
        CommentResponseDTO.CommentDetailDTO parentCommentDTO = CommentConverter.toCommentDetailDTO(parentComment);

        // Convert child comments to DTOs
        List<CommentResponseDTO.CommentDetailDTO> childCommentDTOs = CommentConverter.toCommentDetailDTOList(childComments);

        // Add the parent comment DTO at the beginning of the list
        childCommentDTOs.add(0, parentCommentDTO);

        // Return the final DTO list with next cursor and isLast flag
        return CommentResponseDTO.CommentsListDTO.builder()
                .comments(childCommentDTOs)
                .nextCursor(nextCursor)
                .isLast(isLast)
                .build();
    }


    private Comment getParentComment(Long cursor, Long postId) {
        if (cursor == null) {
            // Fetch the first parent comment if no cursor is provided
            return commentRepository.findFirstByPostIdAndParentIsNull(postId).orElse(null);
        } else {
            // Fetch the parent comment based on the cursor
            return commentRepository.findById(cursor).orElse(null);
        }
    }

    private List<Comment> getChildComments(Comment parentComment, int pageSize) {
        return commentRepository.findByParent(parentComment, PageRequest.of(0, pageSize));
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

    private Long determineNextCursor(Comment parentComment, List<Comment> childComments, int pageSize) {
        if (childComments.size() < pageSize - 1) {
            // 자식 댓글의 수가 페이지 크기보다 적으면, 다음 부모 댓글을 가져온다.
            Optional<Comment> nextParentComment = commentRepository.findNextParentComment(parentComment.getId(), parentComment.getPost().getId());
            return nextParentComment.map(Comment::getId).orElse(null);
        } else {
            // 마지막으로 가져온 자식 댓글의 ID를 다음 커서로 설정한다.
            return childComments.get(childComments.size() - 1).getId();
        }
    }
}



