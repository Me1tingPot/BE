package meltingpot.server.domain.repository;

import meltingpot.server.domain.entity.UserReportCount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserReportCountRepository extends JpaRepository<UserReportCount, Long> {
}
