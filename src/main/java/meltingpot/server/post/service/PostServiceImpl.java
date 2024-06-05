package meltingpot.server.post.service;


import lombok.*;
import meltingpot.server.comment.converter.CommentConverter;
import meltingpot.server.comment.dto.CommentResponseDTO;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.comment.Comment;
import meltingpot.server.domain.entity.post.Post;
import meltingpot.server.domain.entity.enums.PostType;
import meltingpot.server.domain.entity.post.PostImage;
import meltingpot.server.domain.repository.AccountRepository;
import meltingpot.server.domain.repository.CommentRepository;
import meltingpot.server.domain.repository.PostRepository;
import meltingpot.server.post.converter.PostConverter;
import meltingpot.server.post.dto.PostRequestDTO;
import meltingpot.server.post.dto.PostResponseDTO;
import meltingpot.server.util.r2.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static meltingpot.server.post.converter.PostConverter.toPost;
import static meltingpot.server.post.converter.PostConverter.toCreatePostResult;
import static meltingpot.server.post.converter.PostImageConverter.toPostImage;

@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {
    private final AccountRepository accountRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    @Autowired
    private FileService fileService;



    @Override
    public PostResponseDTO.CreatePostResultDTO createPost(PostRequestDTO.CreatePostDTO createPostDTO,Account account){
        Post post = toPost(createPostDTO,account);
        List<String> postImgUrls = Collections.emptyList();
        if (createPostDTO.getImageKeys() != null && !createPostDTO.getImageKeys().isEmpty()) {
            postImgUrls = getCdnUrls(createPostDTO.getImageKeys());
        }
        List<PostImage> postImages = toPostImage(createPostDTO,account, post);
        postRepository.save(post);
        post.setPostImages(postImages);
        return toCreatePostResult(postImgUrls,post);
    }

    private List<String> getCdnUrls(List<String> imageKeys) {
        return imageKeys.stream()
                .map(imageKey -> {
                    String prefix = "post"; // 적절한 prefix 값을 설정
                    return fileService.getCdnUrl(prefix, imageKey);
                })
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public PostResponseDTO.PageDTO getPostsList(PostType postType,Account account,Long cursor, Pageable pageable){
        Page<Post> posts = postRepository.findByPostType(postType,pageable);
       return PostConverter.toPageDTO(posts);
    }


    private CommentResponseDTO.CommentsListDTO getCommentsWithPagination(Long postId, Long cursor, int pageSize) {
        cursor = cursor == null ? Long.MAX_VALUE : cursor;

        List<CommentResponseDTO.ParentCommentDTO> parentCommentDTOs = new ArrayList<>();
        int remainingPageSize = pageSize;
        boolean isLast = true;

        List<Comment> parentComments = commentRepository.findParentCommentsByPostIdAndCursor(postId, cursor, PageRequest.of(0, remainingPageSize + 1));

        for (Comment parentComment : parentComments) {
            if (remainingPageSize <= 0) {
                isLast = false;
                break;
            }

            List<CommentResponseDTO.CommentDetailDTO> childrenCommentDTOs = new ArrayList<>();
            List<Comment> childComments = commentRepository.findChildCommentsByParentId(parentComment.getId(), PageRequest.of(0, remainingPageSize));
            for (Comment childComment : childComments) {
                if (remainingPageSize <= 1) {
                    isLast = false;
                    break;
                }
                childrenCommentDTOs.add(CommentConverter.toCommentDetailDTO(childComment));
                remainingPageSize--;
            }

            parentCommentDTOs.add(CommentConverter.toParentCommentDTO(parentComment, childrenCommentDTOs));
            remainingPageSize--;
        }

        Long nextCursor = isLast ? null : parentComments.get(parentCommentDTOs.size() - 1).getId();

        return CommentConverter.toCommentsListDTO(parentCommentDTOs, nextCursor, isLast);
    }

    private Account findAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }

}

