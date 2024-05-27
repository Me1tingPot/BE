package meltingpot.server.domain.repository.party;

import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.party.PartyParticipant;
import meltingpot.server.domain.entity.party.enums.ParticipantStatus;
import meltingpot.server.domain.entity.party.enums.PartyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyParticipantRepository extends JpaRepository<PartyParticipant, Long> {
    int countByParty_PartyStatusAndParticipantStatusAndAccount(PartyStatus partyStatus, ParticipantStatus participantStatus, Account account);
}
