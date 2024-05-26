package meltingpot.server.domain.repository.party;

import meltingpot.server.domain.entity.party.Party;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PartyRepository extends JpaRepository<Party, Integer>, JpaSpecificationExecutor<Party> {
    Party findByChatRoomId(Long chatRoomId);
}
