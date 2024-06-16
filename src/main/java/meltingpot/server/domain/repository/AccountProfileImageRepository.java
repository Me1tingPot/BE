package meltingpot.server.domain.repository;

import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.AccountProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountProfileImageRepository extends JpaRepository<AccountProfileImage, Long> {
    Optional<AccountProfileImage> findByAccountAndIsThumbnailTrue(Account account);
    List<AccountProfileImage> findAllByAccountAndDeletedAtIsNull(Account account);

    AccountProfileImage findByIdAndDeletedAtIsNull(long id);
    Optional<AccountProfileImage> findByAccountAndSequence(Account account, int sequence);

    int countByAccountAndDeletedAtIsNull(Account account);

}
