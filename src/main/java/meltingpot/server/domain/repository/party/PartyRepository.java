package meltingpot.server.domain.repository.party;

import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.party.Party;
import meltingpot.server.domain.entity.party.enums.PartyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PartyRepository extends JpaRepository<Party, Integer>, JpaSpecificationExecutor<Party> {
    Optional<Party> findByChatRoomId(Long chatRoomId);

    Party findByAccountAndPartyStatus(Account account, PartyStatus status);

    int countByAccountAndPartyStatus(Account account, PartyStatus status);
}
