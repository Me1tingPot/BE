package meltingpot.server.domain.repository;

import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.AccountProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountProfileImageRepository extends JpaRepository<AccountProfileImage, Long> {
    Optional<AccountProfileImage> findByAccountAndIsThumbnailIsTrue(Account account);

}
