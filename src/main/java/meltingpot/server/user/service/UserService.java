package meltingpot.server.user.service;


import lombok.RequiredArgsConstructor;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.AccountProfileImage;
import meltingpot.server.domain.entity.party.enums.ParticipantStatus;
import meltingpot.server.domain.entity.party.enums.PartyStatus;
import meltingpot.server.domain.repository.AccountProfileImageRepository;
import meltingpot.server.domain.repository.AccountRepository;
import meltingpot.server.domain.repository.party.PartyParticipantRepository;
import meltingpot.server.domain.repository.party.PartyRepository;
import meltingpot.server.user.controller.dto.UserResponseDto;
import meltingpot.server.user.service.dto.UpdateBioServiceDto;
import meltingpot.server.user.service.dto.UpdateNameServiceDto;
import meltingpot.server.user.service.dto.UserImagesResponseDto;
import meltingpot.server.util.r2.FileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class UserService {
    private final AccountRepository accountRepository;
    private  final AccountProfileImageRepository accountProfileImageRepository;
    private final PartyRepository partyRepository;
    private final PartyParticipantRepository partyParticipantRepository;
    private final FileService fileService;

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

    public UserResponseDto updateProfileName(Account account, UpdateNameServiceDto serviceDto) {
        account.updateName(serviceDto.getName());
        accountRepository.save(account);

        return readProfile(account);
    }

    public UserResponseDto updateProfileBio(Account account, UpdateBioServiceDto serviceDto) {
        account.updateBio(serviceDto.getBio());
        accountRepository.save(account);

        return readProfile(account);
    }

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
}
