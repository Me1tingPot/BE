package meltingpot.server.user.service;


import lombok.RequiredArgsConstructor;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.AccountProfileImage;
import meltingpot.server.domain.repository.AccountProfileImageRepository;
import meltingpot.server.domain.repository.AccountRepository;
import meltingpot.server.user.controller.dto.UserResponseDto;
import meltingpot.server.user.service.dto.UpdateBioServiceDto;
import meltingpot.server.user.service.dto.UpdateNameServiceDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final AccountRepository accountRepository;
    private  final AccountProfileImageRepository accountProfileImageRepository;

    @Transactional(readOnly = true)
    public UserResponseDto readProfile(Account account) {
        // TODO 프로필 이미지 인스턴스 생성시 주석 풀기
        // 프로필 사진 가져오기
        //AccountProfileImage thumbnail = accountProfileImageRepository.findByAccountAndIsThumbnailIsTrue(account).orElseThrow();
        //return UserResponseDto.of(account,thumbnail);
        return UserResponseDto.of(account);
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
}
