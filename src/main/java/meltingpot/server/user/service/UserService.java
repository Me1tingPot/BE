package meltingpot.server.user.service;


import lombok.RequiredArgsConstructor;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.AccountProfileImage;
import meltingpot.server.domain.entity.comment.Comment;
import meltingpot.server.domain.entity.party.enums.ParticipantStatus;
import meltingpot.server.domain.entity.party.enums.PartyStatus;
import meltingpot.server.domain.entity.post.Post;
import meltingpot.server.domain.repository.AccountProfileImageRepository;
import meltingpot.server.domain.repository.AccountRepository;
import meltingpot.server.domain.repository.CommentRepository;
import meltingpot.server.domain.repository.PostRepository;
import meltingpot.server.domain.repository.party.PartyParticipantRepository;
import meltingpot.server.domain.repository.party.PartyRepository;
import meltingpot.server.party.dto.PartyResponse;
import meltingpot.server.user.controller.dto.NewProfileImageRequestDto;
import meltingpot.server.user.controller.dto.PostResponseDto;
import meltingpot.server.user.controller.dto.UserResponseDto;
import meltingpot.server.user.service.dto.UpdateBioServiceDto;
import meltingpot.server.user.service.dto.UpdateNameServiceDto;
import meltingpot.server.user.service.dto.UserImagesResponseDto;
import meltingpot.server.util.ResponseCode;
import meltingpot.server.util.SliceResponse;
import meltingpot.server.util.r2.FileService;
import meltingpot.server.util.r2.FileUploadResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {
    private final AccountRepository accountRepository;
    private final AccountProfileImageRepository accountProfileImageRepository;
    private final PartyRepository partyRepository;
    private final PartyParticipantRepository partyParticipantRepository;
    private final FileService fileService;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public UserResponseDto readProfile(Account account) {

        // 프로필 사진 가져오기
        String thumbnailUrl = getThumbnailImage(account);

        // 파티 주최 횟수
        int partyHostCnt = partyRepository.countByAccountAndPartyStatus(account, PartyStatus.DONE);

        // 파티 참여 횟수
        int partyParticipateCnt = partyParticipantRepository.countByParty_PartyStatusAndParticipantStatusAndAccount(PartyStatus.DONE, ParticipantStatus.APPROVED, account);

        return UserResponseDto.of(account,thumbnailUrl, partyHostCnt, partyParticipateCnt);
    }

    @Transactional
    public String getThumbnailImage(Account account){
        Optional<AccountProfileImage> thumbnail = accountProfileImageRepository.findByAccountAndIsThumbnailTrue(account);
        if(thumbnail.isEmpty()) {
            System.out.println("에러 발생: 썸네일 사진이 없습니다");
            throw new NoSuchElementException();
        }
        return fileService.getCdnUrl("userProfile-image", thumbnail.get().getImageKey());
    }

    @Transactional
    public UserResponseDto updateProfileName(Account account, UpdateNameServiceDto serviceDto) {
        account.updateName(serviceDto.getName());
        accountRepository.save(account);

        return readProfile(account);
    }

    @Transactional
    public UserResponseDto updateProfileBio(Account account, UpdateBioServiceDto serviceDto) {
        account.updateBio(serviceDto.getBio());
        accountRepository.save(account);

        return readProfile(account);
    }

    @Transactional
    public List<UserImagesResponseDto> readProfileImages( long accountId ) {
        Account account = accountRepository.findByIdAndDeletedAtIsNull(accountId);
        if(account == null) throw new NoSuchElementException();

        List<AccountProfileImage> accountProfileImages = accountProfileImageRepository.findAllByAccountAndDeletedAtIsNull(account);

        List<UserImagesResponseDto> profileImages = new ArrayList<>();

        for( AccountProfileImage image : accountProfileImages ){
            String imageUrl = fileService.getCdnUrl("userProfile-image", image.getImageKey());
            profileImages.add(UserImagesResponseDto.of(image, imageUrl));
        }

        return profileImages;

    }

    @Transactional
    public ResponseCode createNewProfileImage(NewProfileImageRequestDto request, Account account) {

        //TODO 이미 존재하는 시퀀스인지, 프로필 이미지가 3개 이하인지 확인하라

        AccountProfileImage newProfileImage = AccountProfileImage.builder()
                .account(account)
                .imageKey(request.imageKey())
                .sequence(request.sequence())
                .imageOriginalName("")
                .build();

        accountProfileImageRepository.save(newProfileImage);
        return ResponseCode.UPDATE_PROFILE_IMAGE_SUCCESS;
    }

    @Transactional
    public ResponseCode deleteProfileImage(Account account, long imageId) {

        // 프로필 이미지가 2개 이상 있는지 확인
        int image_count = accountProfileImageRepository.countByAccountAndDeletedAtIsNull(account);
        if(image_count < 2) return ResponseCode.PROFILE_IMAGE_LESS_THAN_TWO;

        AccountProfileImage oldProfileImage = accountProfileImageRepository.findById(imageId).orElseThrow();

        // 권한 확인
        if( !oldProfileImage.getAccount().equals(account)){
            return ResponseCode.PROFILE_IMAGE_UPDATE_NOT_OWNER;
        }

        // 삭제하려는 사진이 대표 사진인 경우 다른 사진을 대표 사진으로 임시 설정
        if(oldProfileImage.isThumbnail()){
            List<AccountProfileImage> profileImages = accountProfileImageRepository.findAllByAccountAndDeletedAtIsNull(account);

            for(AccountProfileImage image : profileImages){
                if( !image.equals(oldProfileImage)){
                    image.setThumbnail(true);
                    oldProfileImage.setThumbnail(false);
                    accountProfileImageRepository.save(image);
                    accountProfileImageRepository.save(oldProfileImage);
                    break;
                }
            }
        }

        fileService.deleteFile("userProfile-image", oldProfileImage.getImageKey());
        oldProfileImage.softDelete(LocalDateTime.now());
        accountProfileImageRepository.save(oldProfileImage);

        return ResponseCode.PROFILE_IMAGE_DELETE_SUCCESS;

    }
    // 사용자 이미지 업로드용 presignedUrl 생성
    @Transactional
    public FileUploadResponse generateImageUploadUrl() {
        return fileService.getPreSignedUrl("userProfile-image");
    }

    @Transactional
    public ResponseCode changeThumbnailImage(Account account, long imageId) {
        AccountProfileImage newThumbnailImage = accountProfileImageRepository.findById(imageId).orElseThrow(()-> new IllegalArgumentException("사진이 존재하지 않습니다"));

        if(!newThumbnailImage.getAccount().equals(account)){
            return ResponseCode.PROFILE_IMAGE_UPDATE_NOT_OWNER;
        }

        // 기존 대표 사진 가져오기
        AccountProfileImage oldThumbnailImage = accountProfileImageRepository.findByAccountAndIsThumbnailTrue(account).orElseThrow(()-> new IllegalArgumentException("썸네일이 존재하지 않습니다"));

        // 이미 대표 사진인 경우
        if(newThumbnailImage.equals(oldThumbnailImage)) return ResponseCode.PROFILE_IMAGE_ALREADY_THUMBNAIL;

        oldThumbnailImage.setThumbnail(false);
        accountProfileImageRepository.save(oldThumbnailImage);

        // 새로운 대표 사진 설정
        newThumbnailImage.setThumbnail(true);
        accountProfileImageRepository.save(newThumbnailImage);

        return ResponseCode.PROFILE_CHANGE_THUMBNAIL_SUCCESS;

    }

    @Transactional
    public SliceResponse<PostResponseDto> readUsersPosts(Long userId, Pageable pageable) {
        Account account = accountRepository.findById(userId).orElseThrow();
        return new SliceResponse<>(postRepository.findAllByAccountAndDeletedAtIsNullOrderByIdDesc(account, pageable)
                .map(post -> PostResponseDto.of(post, getThumbnailImage(post.getAccount()))));

    }

    @Transactional
    public SliceResponse<PostResponseDto>  readUsersComments(Long userId, Pageable pageable) {
        Account account = accountRepository.findById(userId).orElseThrow();

        // Post 중복 제거
        Set<Post> uniquePosts = commentRepository.findAllByAccountAndDeletedAtIsNullOrderByIdDesc(account, pageable)
                .stream()
                .map(Comment::getPost)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // uniquePosts를 Pageable로 변환하여 Slice로 생성
        Slice<PostResponseDto> postSlice = uniquePosts.stream()
                .map(post -> PostResponseDto.of(post, getThumbnailImage(post.getAccount())))
                .collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
                    int start = (int) pageable.getOffset();
                    int end = Math.min((start + pageable.getPageSize()), list.size());
                    return new SliceImpl<>(list.subList(start, end), pageable, end < list.size());
                }));

        return new SliceResponse<>(postSlice);
    }

    public SliceResponse<PartyResponse>  readUsersParties(Long userId, Pageable pageable) {
        return null;
    }
}
