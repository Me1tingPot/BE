package meltingpot.server.user.service;


import lombok.RequiredArgsConstructor;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.AccountProfileImage;
import meltingpot.server.domain.entity.enums.Gender;
import meltingpot.server.domain.entity.party.Party;
import meltingpot.server.domain.entity.party.PartyParticipant;
import meltingpot.server.util.Constants;
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
import meltingpot.server.user.controller.dto.UpdateBioRequestDto;
import meltingpot.server.user.controller.dto.UpdateNameRequestDto;
import meltingpot.server.user.controller.dto.UserResponseDto;
import meltingpot.server.user.service.dto.UserImagesResponseDto;
import meltingpot.server.util.ResponseCode;
import meltingpot.server.util.SliceResponse;
import meltingpot.server.util.r2.FileService;
import meltingpot.server.util.r2.FileUploadResponse;
import org.springframework.data.domain.*;
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
            return fileService.getCdnUrl("userProfile-image", Constants.DEFAULT_PROFILE_IMAGE_KEY);
        }
        return fileService.getCdnUrl("userProfile-image", thumbnail.get().getImageKey());
    }

    @Transactional
    public UserResponseDto updateProfileName(Account account, UpdateNameRequestDto serviceDto) {
        account.updateName(serviceDto.nickname());
        accountRepository.save(account);

        return readProfile(account);
    }

    @Transactional
    public UserResponseDto updateProfileBio(Account account, UpdateBioRequestDto serviceDto) {
        account.updateBio(serviceDto.bio());
        accountRepository.save(account);

        return readProfile(account);
    }

    @Transactional
    public List<UserImagesResponseDto> readProfileImages( long accountId ) {
        Account account = accountRepository.findByIdAndIsQuitIsFalse(accountId);
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

        // 입력값 검증[1]: 이미 존재하는 시퀀스인지 확인하기
        if(accountProfileImageRepository.existsByAccountAndSequence(account,request.sequence())){
            throw new IllegalArgumentException("이 자리에는 이미 존재하는 사진이 있습니다.");
        }

        // 입력값 검증[2]: 프로필 사진이 세 개 이하로 있는지 확인하라
        if(accountProfileImageRepository.countByAccountAndDeletedAtIsNull(account)>3){
            throw new IllegalArgumentException("프로필 사진은 네 장 이상 추가할 수 없습니다.");
        }

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

        AccountProfileImage oldProfileImage = accountProfileImageRepository.findById(imageId).orElseThrow(
                ()-> new NoSuchElementException("해당 이미지가 존재하지 않습니다.")
        );

        // 권한 확인
        if( !oldProfileImage.getAccount().equals(account)){
            return ResponseCode.PROFILE_IMAGE_UPDATE_NOT_OWNER;
        }

        // 삭제하려는 사진이 대표 사진인 경우 다른 사진을 대표 사진으로 임시 설정한다.
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
    public SliceResponse<PostResponseDto> readUsersPosts(Long userId, Integer page) {
        Account account = accountRepository.findById(userId).orElseThrow( () -> new NoSuchElementException("계정을 찾을 수 없습니다"));
        PageRequest pageRequest = PageRequest.of(page, Constants.PAGE_DEFAULT_SIZE, Sort.by("createdAt").descending());
        return new SliceResponse<>(postRepository.findAllByAccountAndDeletedAtIsNullOrderByIdDesc(account, pageRequest)
                .map(post -> PostResponseDto.of(post, getThumbnailImage(post.getAccount()))));

    }

    @Transactional
    public SliceResponse<PostResponseDto>  readUsersComments(Long userId, Integer page) {
        Account account = accountRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("계정을 찾을 수 없습니다"));
        PageRequest pageRequest = PageRequest.of(page, Constants.PAGE_DEFAULT_SIZE, Sort.by("createdAt").descending());

        // Post 중복 제거
        Set<Post> uniquePosts = commentRepository.findAllByAccountAndDeletedAtIsNullOrderByIdDesc(account, pageRequest)
                .stream()
                .map(Comment::getPost)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Slice<PostResponseDto> postSlice = uniquePosts.stream()
                .map(post -> PostResponseDto.of(post, getThumbnailImage(post.getAccount())))
                .collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
                    int start = (int) pageRequest.getOffset();
                    int end = Math.min((start + pageRequest.getPageSize()), list.size());
                    return new SliceImpl<>(list.subList(start, end), pageRequest, end < list.size());
                }));

        return new SliceResponse<>(postSlice);
    }

    // 마이페이지 사용자 파티 참여/주최 내역
    @Transactional
    public SliceResponse<PartyResponse> readUsersParties(Long userId, Integer page) {
        Account account = accountRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("계정을 찾을 수 없습니다"));
        PageRequest pageRequest = PageRequest.of(page, Constants.PAGE_DEFAULT_SIZE, Sort.by("createdAt").descending());


        return new SliceResponse<> (partyRepository.findByAccountFromPartyAndPartyParticipant(account,pageRequest)
                .map(party -> PartyResponse.of(party)));

    }

    // 회원 탈퇴
    @Transactional
    public ResponseCode deleteAccount(Account account) {

        EnumSet<PartyStatus> plannedPartyStatus = EnumSet.of(PartyStatus.RECRUIT_SCHEDULED, PartyStatus.RECRUIT_OPEN, PartyStatus.RECRUIT_CLOSED, PartyStatus.RUNNING);

        // 주최 중인 파티 있는지 확인
        if(partyRepository.existsByAccountAndPartyStatusIn(account, plannedPartyStatus)){
            return ResponseCode.PARTY_HOST_ACCOUNT_DELETE_DENIED;
        }

        // 참여 중인 파티 있는지 확인
        boolean hasActiveParty = partyParticipantRepository.findAllByAccount(account).stream()
                .map(participant -> participant.getParty().getPartyStatus())
                .anyMatch(plannedPartyStatus::contains);

        if (hasActiveParty) {
            return ResponseCode.PARTY_PARTICIPANT_ACCOUNT_DELETE_DENIED;
        }

        account.setUsername("");
        account.setLanguages(new ArrayList<>());
        account.setName("UNKNOWN");
        account.setPassword("");
        account.setGender(Gender.UNKNOWN);
        account.setBirth(null);
        account.setBio("This account is deleted");
        account.setNationality("");
        account.setIsQuit(true);

        // 프로필 이미지 삭제
        for(AccountProfileImage image : account.getProfileImages()){
            deleteProfileImage(account, image.getId());
        }

        return ResponseCode.ACCOUNT_DELETE_SUCCESS;
    }
}
