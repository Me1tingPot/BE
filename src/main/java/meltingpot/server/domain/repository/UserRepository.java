package meltingpot.server.domain.repository;

import meltingpot.server.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);

    Optional<User> findByIdAndDeletedIsNull(Long userId);


    Optional<User> findByUsername(String name);

    Optional<User> findByUsernameAndDeletedIsNull(String currentUserName);
}
