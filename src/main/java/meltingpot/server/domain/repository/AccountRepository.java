package meltingpot.server.domain.repository;

import meltingpot.server.domain.entity.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    @EntityGraph(attributePaths = {"accountRoles"})
    Optional<Account> findByUsername(String name);

    Optional<Account> findByUsernameAndDeletedAtIsNull(String currentUserName);

    boolean existsByUsername(String username);
}
