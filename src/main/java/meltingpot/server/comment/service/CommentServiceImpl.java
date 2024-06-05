package meltingpot.server.comment.service;

import lombok.RequiredArgsConstructor;
import meltingpot.server.comment.converter.CommentConverter;
import meltingpot.server.comment.converter.CommentImageConverter;
import meltingpot.server.comment.dto.CommentRequestDTO;
import meltingpot.server.comment.dto.CommentResponseDTO;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.comment.Comment;
import meltingpot.server.domain.entity.comment.CommentImage;
import meltingpot.server.domain.entity.post.Post;
import meltingpot.server.domain.repository.AccountRepositroy;
import meltingpot.server.domain.repository.CommentRepository;
import meltingpot.server.domain.repository.PostRepository;
import meltingpot.server.util.r2.FileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

import static meltingpot.server.comment.converter.CommentConverter.toComment;
import static meltingpot.server.comment.converter.CommentConverter.toCreateCommentResult;
import static meltingpot.server.comment.converter.CommentImageConverter.toCommentImage;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final AccountRepositroy accountRepositroy;
    private final PostRepository postRepository;
    private FileService fileService;

    @Override
    public CommentResponseDTO.CreateCommentResultDTO createComment (CommentRequestDTO.CreateCommentDTO createCommentDTO, Account account, Long postId) {
        Post post = findPostById(postId);
        Comment comment = toComment(createCommentDTO, account, post);
        return processCommentCreation(createCommentDTO, account, comment);
    }

    public CommentResponseDTO.CreateCommentResultDTO createChildComment(CommentRequestDTO.CreateCommentDTO createCommentDTO, Account account, Long commentId) {
        Comment parentComment = findCommentById(commentId);
        Comment childComment = CommentConverter.toChildComment(createCommentDTO, account, parentComment);
        return processCommentCreation(createCommentDTO, account, childComment);
    }

    private CommentResponseDTO.CreateCommentResultDTO processCommentCreation(CommentRequestDTO.CreateCommentDTO createCommentDTO, Account account, Comment comment) {
        List<String> commentImgUrls = Collections.emptyList();
        if (createCommentDTO.getImageKeys() != null && !createCommentDTO.getImageKeys().isEmpty()) {
            commentImgUrls = getCdnUrls(createCommentDTO.getImageKeys());
        }
        List<CommentImage> commentImages = toCommentImage(createCommentDTO, account, comment);
        comment.setCommentImages(commentImages);
        commentRepository.save(comment);

        return toCreateCommentResult(commentImgUrls, comment);
    }
    private List<String> getCdnUrls(List<String> imageKeys) {
        return imageKeys.stream()
                .map(imageKey -> {
                    String prefix = "comment"; // 적절한 prefix 값을 설정
                    return fileService.getCdnUrl(prefix, imageKey);
                })
                .collect(Collectors.toList());
    }

    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() ->  new RuntimeException("댓글을 찾을 수 없습니다."));
    }


    private Account findAccountById(Long accountId) {
        return accountRepositroy.findById(accountId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시물을 찾을 수 없습니다."));
    }

}
