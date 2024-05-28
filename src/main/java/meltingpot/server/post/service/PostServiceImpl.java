package meltingpot.server.post.service;

import lombok.RequiredArgsConstructor;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.Post;
import meltingpot.server.domain.entity.enums.PostType;
import meltingpot.server.domain.repository.AccountRepositroy;
import meltingpot.server.domain.repository.PostRepository;
import meltingpot.server.post.converter.PostConverter;
import meltingpot.server.post.dto.PostRequestDTO;
import meltingpot.server.post.dto.PostResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import java.util.List;

import static meltingpot.server.post.converter.PostConverter.toPageDTO;
import static meltingpot.server.post.converter.PostConverter.toPost;

@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {
    private final AccountRepositroy accountRepositroy;
    private final PostRepository postRepository;


    @Override
    public void createPost(PostRequestDTO.CreatePostDTO createPostDTO,Account account){
        Post post = toPost(createPostDTO,account);
        postRepository.save(post);

//        for (MultipartFile file : multipartFileList) {
//            String uuid = UUID.randomUUID().toString();
//            Uuid savedUuid = uuidRepository.save(Uuid.builder()
//                    .uuid(uuid).build());
//
//            String pictureUrl = s3Manager.uploadFile(s3Manager.generatePostKeyName(savedUuid, file.getOriginalFilename()), file);
//
//            PostImage postImage = PostImageConverter.toPostImage(savedPost, file, pictureUrl);
//            postImageRepository.save(postImage);
//        }
    }


    @Override
    @Transactional(readOnly = true)
    public PostResponseDTO.PageDTO getPostsList(PostType postType, Account account,Long cursor, Pageable pageable){
        Page<Post> posts = postRepository.findByPostType(postType,pageable);
       return PostConverter.toPageDTO(posts);
    }


    private Account findAccountById(Long accountId) {
        return accountRepositroy.findById(accountId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }

}

