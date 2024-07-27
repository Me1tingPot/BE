package meltingpot.server.post.service;


import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import meltingpot.server.comment.dto.CommentsListResponse;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.comment.Comment;
import meltingpot.server.domain.entity.post.Post;
import meltingpot.server.domain.entity.enums.PostType;
import meltingpot.server.domain.entity.post.PostImage;
import meltingpot.server.domain.repository.AccountRepository;
import meltingpot.server.domain.repository.CommentRepository;
import meltingpot.server.domain.repository.PostImageRepository;
import meltingpot.server.domain.repository.PostRepository;
import meltingpot.server.post.dto.PostCreateRequest;
import meltingpot.server.post.dto.PostDetailResponse;
import meltingpot.server.post.dto.PostsListResponse;
import meltingpot.server.util.ResponseCode;
import meltingpot.server.util.r2.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import static meltingpot.server.util.ResponseCode.POST_NOT_FOUND;


@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final AccountRepository accountRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostImageRepository postImageRepository;
    @Autowired
    private FileService fileService;


    /*post 작성하기*/
    public ResponseCode createPost(PostCreateRequest postCreateRequest, Account account, boolean isDraft) {
        Optional<Post> optionalDraft = getDraftPost(account);
        Post post = optionalDraft.orElseGet(() -> postCreateRequest.toEntity(account));
        System.out.println("Post  " + post.getId() + post.getTitle() + post.getIsDraft());

        if (optionalDraft.isPresent()) {
            updatePostContent(post, account, postCreateRequest);
        }
        setPostImages(post, account, postCreateRequest);
        // isDraft 값을 설정하기 전에 현재 상태 출력
        System.out.println("Setting isDraft for post with ID " + post.getId() + " to " + isDraft);
        post.setIsDraft(isDraft);

        // isDraft 값 설정 후 출력
        System.out.println("Post isDraft status: " + post.getIsDraft());

        postRepository.save(post);

        return isDraft ? ResponseCode.DRAFT_SAVE_SUCCESS : ResponseCode.CREATE_POST_SUCCESS;
    }


    /*post 수정하기*/
    public ResponseCode updatePost(PostCreateRequest updateRequest,Long postId, Account account){
        Post post = findPostById(postId);
        isAuthenticated ( post, account);
        updatePostContent(post, account, updateRequest);
        setPostImages(post,account,updateRequest);

        postRepository.save(post);

        return ResponseCode.UPDATE_POST_SUCCESS;
    }

    /*post 내용 불러오기*/
    @Transactional(readOnly = true)
    public PostDetailResponse getPostDetail(Long postId, Long cursor, int pageSize){
        Post post = findPostById(postId);
        CommentsListResponse commentsList = fetchCommentsList(postId, cursor, pageSize);
        return PostDetailResponse.of(post,commentsList);
    }

    /*post 목록 불러오기*/
    @Transactional(readOnly = true)
    public PostsListResponse getPostsList(Account account, PostType postType,  Long cursor, int pageSize){
        Pageable pageable = PageRequest.of(0, pageSize);
        List<Post> posts = postRepository.findByPostTypeAndCursor(postType, cursor, pageable);
        Long nextCursor = posts.isEmpty() ? null : posts.get(posts.size() - 1).getId();
        boolean isLast = posts.size() < pageSize;
        return PostsListResponse.from(posts, nextCursor, isLast);
    }

    /*post 삭제하기*/
    public ResponseCode deletePost(Long postId, Account account){
        Post post = findPostById(postId);
        isAuthenticated ( post, account);

        // 게시물에 연관된 이미지 삭제
        if (!post.getPostImages().isEmpty()) {
            postImageRepository.deleteAll(post.getPostImages());
        }

        // 게시물에 연관된 댓글 삭제
        if (!post.getComments().isEmpty()) {
            commentRepository.deleteAll(post.getComments());
        }

        // 게시물 삭제
        postRepository.delete(post);

        return ResponseCode.POST_DELETE_SUCCESS;

    }

    /* 임시저장된 글 불러오기 */
    public PostDetailResponse getTempPost (Account account ){
        Optional<Post> optionalDraft = getDraftPost(account);
        if(optionalDraft.isPresent()){
            Post draftPost = optionalDraft.get();
            return PostDetailResponse.from(draftPost);
        }else{
            throw new NoSuchElementException(ResponseCode.POST_NOT_FOUND.getDetail());
        }
    }



    private Account findAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }

    private Post findPostById(Long postId) {
        return  postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
    }

    private void isAuthenticated (Post post, Account account) {
        if (!post.getAccount().getId().equals(account.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }
    }

    private List<String> getCdnUrls(List<String> imageKeys) {
        return imageKeys.stream()
                .map(imageKey -> {
                    String prefix = "post"; // 적절한 prefix 값을 설정
                    return fileService.getCdnUrl(prefix, imageKey);
                })
                .collect(Collectors.toList());
    }

    private void updatePostContent(Post post, Account account, PostCreateRequest updateRequest) {
        post.setTitle(updateRequest.getTitle());
        post.setContent(updateRequest.getContent());

        // 기존의 모든 PostImage 삭제
        if (post.getPostImages() != null && !post.getPostImages().isEmpty()) {
            postImageRepository.deleteAll(post.getPostImages());
            post.getPostImages().clear();
        }
    }



    private List<PostImage> createPostImages(List<String> imageKeys, Post post, Account account) {
        List<String> postImgUrls = getCdnUrls(imageKeys);
        return postImgUrls.stream()
                .map(imageUrl -> PostImage.builder()
                        .imageUrl(imageUrl)
                        .post(post)
                        .account(account)
                        .build())
                .collect(Collectors.toList());
    }

    private CommentsListResponse fetchCommentsList(Long postId, Long cursor, int pageSize) {
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

    private Optional<Post> getDraftPost(Account account) {
        return postRepository.findByAccountAndIsDraftTrue(account);
    }

    private void setPostImages(Post post, Account account,PostCreateRequest postRequest) {
        List<PostImage> postImages = createPostImages(postRequest.getImageKeys(), post, account);
        post.setPostImages(postImages);
    }
}

