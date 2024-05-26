package meltingpot.server.comment.service;

import lombok.RequiredArgsConstructor;
import meltingpot.server.comment.converter.CommentConverter;
import meltingpot.server.comment.dto.CommentRequestDTO;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.Comment;
import meltingpot.server.domain.entity.Post;
import meltingpot.server.domain.repository.AccountRepositroy;
import meltingpot.server.domain.repository.CommentRepository;
import meltingpot.server.domain.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final AccountRepositroy accountRepositroy;
    private final PostRepository postRepository;

    @Override
    public void createComment(CommentRequestDTO.CreateCommentDTO createCommentDTO,Long accountId,Long postId){
        Account account = findAccountById(accountId);
        Post post = findPostById(postId);
        Comment comment = CommentConverter.toComment(createCommentDTO,account,post);
        commentRepository.save(comment);
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
