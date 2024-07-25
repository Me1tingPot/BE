package meltingpot.server.post.service;


import lombok.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import meltingpot.server.comment.dto.CommentsListResponse;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.comment.Comment;
import meltingpot.server.domain.entity.post.Post;
import meltingpot.server.domain.entity.enums.PostType;
import meltingpot.server.domain.entity.post.PostImage;
import meltingpot.server.domain.repository.AccountRepository;
import meltingpot.server.domain.repository.CommentRepository;
import meltingpot.server.domain.repository.PostRepository;
import meltingpot.server.post.dto.PostCreateRequest;
import meltingpot.server.post.dto.PostDetailResponse;
import meltingpot.server.post.dto.PostsListResponse;
import meltingpot.server.util.ResponseCode;
import meltingpot.server.util.r2.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;



@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final AccountRepository accountRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    @Autowired
    private FileService fileService;


    /*post 작성하기*/
    public ResponseCode createPost(PostCreateRequest createPostDTO,Account account){
        Post post = createPostDTO.toEntity(account);
        List<String> postImgUrls = Collections.emptyList();
        if (createPostDTO.getImageKeys() != null && !createPostDTO.getImageKeys().isEmpty()) {
            postImgUrls = getCdnUrls(createPostDTO.getImageKeys());
        }
        List<PostImage> postImages = postImgUrls.stream()
                .map(imageUrl->PostImage.builder()
                        .imageUrl(imageUrl)
                        .post(post)
                        .account(account)
                        .build())
                .collect(Collectors.toList());
        post.setPostImages(postImages);
        postRepository.save(post);
        return ResponseCode.POST_CREATE_SUCCESS;
    }


    /*post 내용 불러오기*/
    @Transactional(readOnly = true)
    public PostDetailResponse getPostDetail(Long postId, Long cursor, int pageSize){
        Post post = findPostById(postId);

        // 댓글 목록 가져오기
        CommentsListResponse commentsList = fetchCommentsList(postId, cursor, pageSize);
        return PostDetailResponse.of(post,commentsList);
    }

    /*post 목록 불러오기*/
    @Transactional(readOnly = true)
    public PostsListResponse getPostsList(Account account, PostType postType,  Long cursor, int pageSize){
        Pageable pageable = PageRequest.of(0, pageSize);
        Long nextCursor = null;
        boolean isLast = false;
        List<Post> posts = postRepository.findByPostTypeAndCursor(postType, cursor, pageable);

        if (posts.size() > 0) {
            nextCursor = posts.get(posts.size() - 1).getId();
            isLast = posts.size() < pageSize;
        } else {
            isLast = true;
        }

        return PostsListResponse.from(posts, nextCursor, isLast);
    }



    private Account findAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }

    private Post findPostById(Long postId) {
        return  postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
    }

    private List<String> getCdnUrls(List<String> imageKeys) {
        return imageKeys.stream()
                .map(imageKey -> {
                    String prefix = "post"; // 적절한 prefix 값을 설정
                    return fileService.getCdnUrl(prefix, imageKey);
                })
                .collect(Collectors.toList());
    }

    private CommentsListResponse fetchCommentsList(Long postId, Long cursor, int pageSize) {
        List<CommentsListResponse.CommentDetail> commentDetails = new ArrayList<>();
        int count = 0;
        Long parentCursor = null;

        // Handle case where cursor points to a child comment
        if (cursor != null) {
            Comment childComment = commentRepository.findById(cursor).orElse(null);
            if (childComment != null && childComment.getParent() != null) {
                Comment parentComment = childComment.getParent();
                List<Comment> remainingChildren = commentRepository.findChildrenCommentsByParentId(parentComment.getId(), cursor);
                for (Comment child : remainingChildren) {
                    if (count >= pageSize) break;
                    commentDetails.add(CommentsListResponse.CommentDetail.from(child));
                    count++;
                }
                parentCursor = parentComment.getId();

                // If there are more parent comments to fetch after children
                if (count < pageSize && remainingChildren.size() < pageSize) {
                    parentCursor = parentComment.getId();
                }
            } else {
                parentCursor = cursor; // cursor points to a parent comment
            }
        }

        // Fetch parent comments and their children
        if (count < pageSize) {
            Pageable pageable = PageRequest.of(0, pageSize - count);
            List<Comment> parentComments = commentRepository.findParentCommentsByPostId(postId, parentCursor, pageable);
            for (Comment parent : parentComments) {
                if (count >= pageSize) break;
                commentDetails.add(CommentsListResponse.CommentDetail.from(parent));
                count++;

                List<Comment> children = commentRepository.findChildrenCommentsByParentId(parent.getId(), null);
                for (Comment child : children) {
                    if (count >= pageSize) break;
                    commentDetails.add(CommentsListResponse.CommentDetail.from(child));
                    count++;
                }
            }
        }

        Long nextCursor = (count < pageSize) ? null : commentDetails.get(commentDetails.size() - 1).getCommentId();
        boolean isLast = (count < pageSize);

        return CommentsListResponse.from(commentDetails, nextCursor, isLast);
    }


}

