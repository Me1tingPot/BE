package meltingpot.server.user.service;


import lombok.RequiredArgsConstructor;
import meltingpot.server.auth.controller.dto.ProfileImageRequestDto;
import meltingpot.server.comment.service.CommentService;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.AccountProfileImage;
import meltingpot.server.domain.entity.party.enums.ParticipantStatus;
import meltingpot.server.domain.entity.party.enums.PartyStatus;
import meltingpot.server.domain.entity.post.Post;
import meltingpot.server.domain.repository.AccountProfileImageRepository;
import meltingpot.server.domain.repository.AccountRepository;
import meltingpot.server.domain.repository.CommentRepository;
import meltingpot.server.domain.repository.PostRepository;
import meltingpot.server.domain.repository.party.PartyParticipantRepository;
import meltingpot.server.domain.repository.party.PartyRepository;
import meltingpot.server.domain.specification.party.PartySpecification;
import meltingpot.server.party.dto.PartyResponse;
import meltingpot.server.post.service.PostService;
import meltingpot.server.user.controller.dto.NewProfileImageRequestDto;
import meltingpot.server.user.controller.dto.PostResponse;
import meltingpot.server.user.controller.dto.UserDetailRequestDto;
import meltingpot.server.user.controller.dto.UserResponseDto;
import meltingpot.server.user.service.dto.UpdateBioServiceDto;
import meltingpot.server.user.service.dto.UpdateNameServiceDto;
import meltingpot.server.user.service.dto.UserImagesResponseDto;
import meltingpot.server.util.PageResponse;
import meltingpot.server.util.ResponseCode;
import meltingpot.server.util.r2.FileService;
import meltingpot.server.util.r2.FileUploadResponse;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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
        AccountProfileImage thumbnail = accountProfileImageRepository.findByAccountAndIsThumbnailTrue(account).orElseThrow();
        String thumbnailUrl = fileService.getCdnUrl("userProfile-image", thumbnail.getImageKey());

        // 파티 주최 횟수
        int partyHostCnt = partyRepository.countByAccountAndPartyStatus(account, PartyStatus.DONE);

        // 파티 참여 횟수
        int partyParticipateCnt = partyParticipantRepository.countByParty_PartyStatusAndParticipantStatusAndAccount(PartyStatus.DONE, ParticipantStatus.APPROVED, account);

        return UserResponseDto.of(account,thumbnailUrl, partyHostCnt, partyParticipateCnt);
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

    public ResponseCode changeThumbnailImage(Account account, long imageId) {
        AccountProfileImage newThumbnailImage = accountProfileImageRepository.findById(imageId).orElseThrow();

        if(!newThumbnailImage.getAccount().equals(account)){
            return ResponseCode.PROFILE_IMAGE_UPDATE_NOT_OWNER;
        }

        // 기존 대표 사진 가져오기
        AccountProfileImage oldThumbnailImage = accountProfileImageRepository.findByAccountAndIsThumbnailTrue(account).orElseThrow();

        // 이미 대표 사진인 경우
        if(newThumbnailImage.equals(oldThumbnailImage)) return ResponseCode.PROFILE_IMAGE_ALREADY_THUMBNAIL;

        oldThumbnailImage.setThumbnail(false);
        accountProfileImageRepository.save(oldThumbnailImage);

        // 새로운 대표 사진 설정
        newThumbnailImage.setThumbnail(true);
        accountProfileImageRepository.save(newThumbnailImage);

        return ResponseCode.PROFILE_CHANGE_THUMBNAIL_SUCCESS;

    }

    public PageResponse<PostResponse> readUsersPosts(UserDetailRequestDto userDetailRequestDto) {
        return null;
    }

    public PageResponse<PostResponse>  readUsersComments(UserDetailRequestDto userDetailRequestDto) {
        return null;
    }

    public PageResponse<PartyResponse>  readUsersParties(UserDetailRequestDto userDetailRequestDto) {
        return null;
    }
}
