package meltingpot.server.domain.repository.party;

import meltingpot.server.domain.entity.party.Party;
import meltingpot.server.domain.entity.party.PartyParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PartyParticipantRepository extends JpaRepository<PartyParticipant, Long> {
    int countByPartyId(Long partyId);
    int countByUserId(Long userId);
    List<PartyParticipant> findAllByUserId(Long userId);
}
