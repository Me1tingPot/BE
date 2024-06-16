package meltingpot.server.domain.repository;

import meltingpot.server.domain.entity.MailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MailVerificationRepository extends JpaRepository<MailVerification, Long> {
    @Query(value = "select * from mail_verification where email = :email and expired_at >= :now and verified = false", nativeQuery = true)
    Optional<MailVerification> findByEmailAndExpiredAtIsAfterNowAndVerifiedFalse(String email, LocalDateTime now);

    boolean existsByEmailAndVerifiedTrue(String email);

}
