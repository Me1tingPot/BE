package meltingpot.server.domain.repository.party;

import meltingpot.server.domain.entity.party.Party;
import meltingpot.server.domain.entity.party.PartyParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyRepository extends JpaRepository<Party, Long> {
    Party findByChatRoomId(Long chatRoomId);
}
