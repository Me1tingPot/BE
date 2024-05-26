package meltingpot.server.post.service;

import lombok.RequiredArgsConstructor;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.Post;
import meltingpot.server.domain.entity.common.AmazonS3Manager;
import meltingpot.server.domain.entity.common.UuidRepository;
import meltingpot.server.domain.repository.AccountRepositroy;
import meltingpot.server.domain.repository.PostImageRepository;
import meltingpot.server.domain.repository.PostRepository;
import meltingpot.server.post.dto.PostRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static meltingpot.server.post.converter.PostConverter.toPost;

@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {
    private final AccountRepositroy accountRepositroy;
    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final UuidRepository uuidRepository;

    @Override
    public void createPost(PostRequestDTO.CreatePostDTO createPostDTO,Long accountId){
        Account account = findAccountById(accountId);
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

    private Account findAccountById(Long accountId) {
        return accountRepositroy.findById(accountId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }
}

