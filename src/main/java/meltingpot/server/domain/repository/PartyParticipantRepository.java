package meltingpot.server.domain.repository;

import meltingpot.server.domain.entity.party.PartyParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PartyParticipantRepository extends JpaRepository<PartyParticipant, Long> {
    List<PartyParticipant> findAllByPartyId(int partyId);
}
