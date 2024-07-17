package meltingpot.server.domain.repository.party;

import meltingpot.server.domain.entity.party.PartyReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyReportRepository extends JpaRepository<PartyReport, Integer> {
    PartyReport findAllByPartyId(Integer partyId);
}
