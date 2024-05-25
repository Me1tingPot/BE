package meltingpot.server.domain.repository;
import java.util.Optional;

import meltingpot.server.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenValue(String tokenValue);

    RefreshToken getByTokenValue(String tokenValue);

    @Modifying
    @Query("delete from RefreshToken where tokenValue = :tokenValue")
    void deleteByTokenValue(@Param(value = "tokenValue") String tokenValue);

    Boolean existsByTokenValue(String tokenValue);

}