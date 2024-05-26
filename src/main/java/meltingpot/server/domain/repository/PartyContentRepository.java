package meltingpot.server.domain.repository;

import meltingpot.server.domain.entity.party.PartyContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PartyContentRepository extends JpaRepository<PartyContent, Long> {
    List<PartyContent> findAllByPartyId(int partyId);
}
