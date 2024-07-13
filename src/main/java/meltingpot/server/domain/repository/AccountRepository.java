package meltingpot.server.domain.repository;

import meltingpot.server.domain.entity.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUsernameAndIsQuitIsFalse(String name);

    boolean existsByUsername(String username);

    Account findByIdAndDeletedAtIsNull(Long id);
}
