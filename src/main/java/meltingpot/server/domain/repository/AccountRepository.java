package meltingpot.server.domain.repository;

import meltingpot.server.domain.entity.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUsernameAndIsQuitIsFalse(String username);
    Optional<Account> findByUsername(String name);

    boolean existsByUsername(String username);

    Account findByIdAndIsQuitIsFalse(Long id);
}
