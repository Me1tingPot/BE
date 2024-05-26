package meltingpot.server.domain.repository.party;

import meltingpot.server.domain.entity.party.PartyParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyParticipantRepository extends JpaRepository<PartyParticipant, Long> {
}
