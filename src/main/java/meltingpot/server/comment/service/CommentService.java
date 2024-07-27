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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
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


    /* 댓글 작성 */
    public ResponseCode createComment(CommentCreateRequest commentCreateRequest , Account account, Long postId) {
        Post post = findPostById(postId);
        Comment comment = commentCreateRequest.toEntity(post, account, null);

        if (commentCreateRequest.getImageKey() != null && !commentCreateRequest.getImageKey().isEmpty()) {
            String commentImgUrl = getCdnUrl(commentCreateRequest.getImageKey());
            createCommentImage(comment, account, commentImgUrl);
        }

        commentRepository.save(comment);
        return ResponseCode.CREATE_COMMENT_SUCCESS;
    }

    /* 대댓글 작성 */
    public ResponseCode createChildComment(CommentCreateRequest commentCreateRequest, Account account, Long parentCommentId) {
        Comment parentComment = findCommentById(parentCommentId);
        Post post = parentComment.getPost();
        Comment childComment = commentCreateRequest.toEntity(post, account, parentComment);

        if (commentCreateRequest.getImageKey() != null && !commentCreateRequest.getImageKey().isEmpty()) {
            String commentImgUrl = getCdnUrl(commentCreateRequest.getImageKey());
            createCommentImage(childComment, account, commentImgUrl);
        }

        commentRepository.save(childComment);
        return ResponseCode.CREATE_CHILD_COMMENT_SUCCESS;
    }

    /* 댓글 수정 */
    public ResponseCode updateComment(CommentCreateRequest updateCommentDTO, Account account, Long commentId) {
        Comment comment = findCommentById(commentId);
        comment.setContent(updateCommentDTO.getContent());

        String newImageKey = updateCommentDTO.getImageKey();
        updateCommentImage(comment, account, newImageKey);

        commentRepository.save(comment);
        return ResponseCode.UPDATE_COMMENT_SUCCESS;
    }

    /* 댓글 목록 불러오기 */
    @Transactional(readOnly = true)
    public CommentsListResponse getCommentsList(Account account, Long postId, Long cursor, int pageSize) {
        List<CommentsListResponse.CommentDetail> commentDetailDTOs = new ArrayList<>();
        int count = 0;
        Long parentCursor = null;

        // Cursor가 자식 댓글에 해당하는 경우 처리
        if (cursor != null) {
            Comment cursorComment = commentRepository.findById(cursor).orElse(null);
            if (cursorComment != null) {
                Comment parentComment;
                if (cursorComment.getParent() != null) {
                    // Cursor가 자식 댓글을 나타내는 경우
                    parentComment = cursorComment.getParent();
                } else {
                    // Cursor가 부모 댓글을 나타내는 경우
                    parentComment = cursorComment;
                }

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

        return CommentsListResponse.from(commentDetailDTOs, nextCursor, isLast);
    }


    /* 댓글 삭제하기 */
    @Transactional
    public ResponseCode deleteComment(Long commentId, Account account) {
        Comment comment = findCommentById(commentId);

        if (!comment.getAccount().getId().equals(account.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "삭제 권한이 없습니다.");
        }
        if (comment.getParent() == null) {
            comment.setContent("삭제된 댓글입니다.");
            comment.setAccount(null);
            comment.setIsAnonymous(true);

            if (comment.getCommentImage() != null) {
                CommentImage commentImage = comment.getCommentImage();
                comment.setCommentImage(null);
                commentImageRepository.delete(commentImage);
            }
            // 자식 댓글이 남아있지 않으면 부모 댓글도 삭제
            if (comment.getChildren().isEmpty()) {
                commentRepository.delete(comment);
            }


        } else {
            Comment parentComment = comment.getParent();
            commentRepository.delete(comment);
            parentComment.getChildren().remove(comment);
            if (parentComment.getChildren().isEmpty()) {
                commentRepository.delete(parentComment);
            }
        }
        return ResponseCode.COMMENT_DELETE_SUCCESS;
    }

    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시물을 찾을 수 없습니다."));
    }

    private String getCdnUrl(String imageKey) {
        String prefix = "comment"; // 적절한 prefix 값을 설정
        return fileService.getCdnUrl(prefix, imageKey);
    }

    private void createCommentImage(Comment comment, Account account, String imageUrl) {
        CommentImage commentImage = CommentImage.builder()
                .imageUrl(imageUrl)
                .comment(comment)
                .account(account)
                .build();
        comment.setCommentImage(commentImage);
    }

    private void updateCommentImage(Comment comment, Account account, String newImageKey) {
        CommentImage oldCommentImage = comment.getCommentImage();
        if (newImageKey == null || newImageKey.isEmpty()) {
            if (oldCommentImage != null) {
                commentImageRepository.delete(oldCommentImage);
                comment.setCommentImage(null);
            }
        } else {
            String newImageUrl = getCdnUrl(newImageKey);
            if (oldCommentImage != null) {
                oldCommentImage.setImageUrl(newImageUrl);
                commentImageRepository.save(oldCommentImage);
            } else {
                createCommentImage(comment, account, newImageUrl);
            }
        }
    }

}



