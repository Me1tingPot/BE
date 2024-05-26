package meltingpot.server.party.service;

import lombok.RequiredArgsConstructor;
import meltingpot.server.domain.repository.PartyRepository;
import meltingpot.server.party.dto.PartyResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PartyService {
    private final PartyRepository partyRepository;

    @Transactional
    public PartyResponse getParty(Long partyId) {
        return partyRepository.findById(partyId).map(PartyResponse::of).orElseThrow();
    }
}
