package meltingpot.server.domain.repository;

import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.AccountProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface AccountProfileImageRepository extends JpaRepository<AccountProfileImage, Long> {
    Optional<AccountProfileImage> findByAccountAndIsThumbnailTrue(Account account);
    List<AccountProfileImage> findAllByAccountAndDeletedAtIsNull(Account account);

}
