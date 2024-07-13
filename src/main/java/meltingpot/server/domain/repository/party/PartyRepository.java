package meltingpot.server.domain.repository.party;

import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.party.Party;
import meltingpot.server.domain.entity.party.enums.PartyStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.EnumSet;
import java.util.Optional;

public interface PartyRepository extends JpaRepository<Party, Integer>, JpaSpecificationExecutor<Party> {
    Optional<Party> findByChatRoomId(Long chatRoomId);

    Party findByAccountAndPartyStatus(Account account, PartyStatus status);

    boolean existsByAccountAndPartyStatusIn(Account account, EnumSet<PartyStatus> statuses);

    int countByAccountAndPartyStatus(Account account, PartyStatus status);

    @Query("SELECT DISTINCT p FROM Party p LEFT JOIN p.partyParticipants pp WHERE (p.account = :account OR pp.account = :account) AND p.deletedAt IS NULL ORDER BY p.createdAt DESC")
    Slice<Party> findByAccountFromPartyAndPartyParticipant(@Param("account") Account account, Pageable pageable);
}
