package meltingpot.server.comment.service;

import lombok.RequiredArgsConstructor;
import meltingpot.server.comment.dto.CommentCreateRequest;
import meltingpot.server.comment.dto.CommentsListResponse;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.comment.Comment;
import meltingpot.server.domain.entity.comment.CommentImage;
import meltingpot.server.domain.entity.post.Post;
import meltingpot.server.domain.repository.AccountRepository;
import meltingpot.server.domain.repository.CommentImageRepository;
import meltingpot.server.domain.repository.CommentRepository;
import meltingpot.server.domain.repository.PostRepository;
import meltingpot.server.util.ResponseCode;
import meltingpot.server.util.r2.FileService;
import org.springframework.data.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService  {
    private final CommentRepository commentRepository;
    private final AccountRepository accountRepository;
    private final PostRepository postRepository;
    private final CommentImageRepository commentImageRepository;

    @Autowired
    private FileService fileService;



    public ResponseCode createComment(CommentCreateRequest commentCreateRequest , Account account, Long postId) {
        Post post = findPostById(postId);
        String commentImgUrl = null;
        if (commentCreateRequest.getImageKey() != null){
            commentImgUrl = getCdnUrl(commentCreateRequest.getImageKey());
        }
        Comment comment = commentCreateRequest.toEntity(post, account, null);
        if (commentCreateRequest.getImageKey() == null || commentCreateRequest.getImageKey().isEmpty()) {
            return null;
        }

        if (commentImgUrl != null) {
            CommentImage commentImage = CommentImage.builder()
                    .imageUrl(commentImgUrl)
                    .comment(comment)
                    .account(account)
                    .build();
            comment.setCommentImage(commentImage);
        }
        commentRepository.save(comment);
        return ResponseCode.CREATE_COMMENT_SUCCESS;
    }


    public ResponseCode createChildComment(CommentCreateRequest commentCreateRequest, Account account, Long commentId) {
        Comment parentComment = findCommentById(commentId);
        Post post = findPostById(parentComment.getPost().getId());
        String commentImgUrl = null;
        if (commentCreateRequest.getImageKey() != null) {
            commentImgUrl = getCdnUrl(commentCreateRequest.getImageKey());
        }
        Comment childComment = commentCreateRequest.toEntity(post, account, parentComment);

        if (commentImgUrl != null) {
            CommentImage commentImage = CommentImage.builder()
                    .imageUrl(commentImgUrl)
                    .comment(childComment)
                    .account(account)
                    .build();
            childComment.setCommentImage(commentImage);
        }

        commentRepository.save(childComment);
        return ResponseCode.CREATE_CHILD_COMMENT_SUCCESS;
    }


    public ResponseCode updateComment(CommentCreateRequest updateCommentDTO, Account account, Long commentId) {
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
            String newImageUrl = getCdnUrl(newImageKey);
            if (oldCommentImage != null) {
                oldCommentImage.setImageUrl(newImageKey);
                commentImageRepository.save(oldCommentImage);
            } else {
                CommentImage newCommentImage = CommentImage.builder()
                        .imageUrl(newImageUrl)
                        .comment(comment)
                        .account(account)
                        .build();
                comment.setCommentImage(newCommentImage);
                commentImageRepository.save(newCommentImage);
            }
        }
        // 댓글 저장
        commentRepository.save(comment);
        return ResponseCode.UPDATE_COMMENT_SUCCESS;
    }

    @Transactional(readOnly = true)
    public CommentsListResponse getCommentsList(Account account, Long postId, Long cursor, int pageSize) {
        List<CommentsListResponse.CommentDetail> commentDetailDTOs = new ArrayList<>();
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
                    commentDetailDTOs.add(CommentsListResponse.CommentDetail.from(child));
                    count++;
                }
                parentCursor = parentComment.getId();

                // 만약 자식 댓글을 모두 가져왔고, 페이지가 꽉 차지 않았다면 다음 부모 댓글로 넘어감
                if (count < pageSize && remainingChildren.size() < pageSize) {
                    parentCursor = parentComment.getId();
                }
            } else {
                parentCursor = cursor; // cursor가 부모 댓글인 경우
            }
        }

        // 부모 댓글과 자식 댓글을 가져오는 처리
        if (count < pageSize) {
            Pageable pageable = PageRequest.of(0, pageSize - count);
            List<Comment> parentComments = commentRepository.findParentCommentsByPostId(postId, parentCursor, pageable);
            for (Comment parent : parentComments) {
                if (count >= pageSize) break;
                commentDetailDTOs.add(CommentsListResponse.CommentDetail.from(parent));
                count++;

                List<Comment> children = commentRepository.findChildrenCommentsByParentId(parent.getId(), null);
                for (Comment child : children) {
                    if (count >= pageSize) break;
                    commentDetailDTOs.add(CommentsListResponse.CommentDetail.from(child));
                    count++;
                }
            }
        }

        Long nextCursor = (count < pageSize) ? null : commentDetailDTOs.get(commentDetailDTOs.size() - 1).getCommentId();
        boolean isLast = (count < pageSize);

        return CommentsListResponse.from(commentDetailDTOs,nextCursor,isLast);
    }



    private String getCdnUrl(String imageKey) {
        String prefix = "comment"; // 적절한 prefix 값을 설정
        return fileService.getCdnUrl(prefix, imageKey);
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



