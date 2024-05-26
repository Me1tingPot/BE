package meltingpot.server.party.service;

import lombok.RequiredArgsConstructor;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.party.PartyParticipant;
import meltingpot.server.domain.entity.party.enums.ParticipantStatus;
import meltingpot.server.domain.entity.party.enums.PartyStatus;
import meltingpot.server.domain.repository.party.PartyParticipantRepository;
import meltingpot.server.domain.repository.party.PartyRepository;
import meltingpot.server.party.dto.PartyResponse;
import meltingpot.server.util.ResponseCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import meltingpot.server.domain.entity.party.Party;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PartyService {
    private final PartyRepository partyRepository;
    private final PartyParticipantRepository partyParticipantRepository;

    @Transactional
    public PartyResponse getParty(int partyId, Account user) {
        Party party = partyRepository.findById(partyId).orElseThrow();
        PartyStatus partyStatus = party.getPartyStatus();

        boolean isPartyOwner = party.getAccount().equals(user);

        if ((!isPartyOwner && partyStatus == PartyStatus.TEMP_SAVED) || partyStatus == PartyStatus.CANCELED) {
            throw new NoSuchElementException();
        }

        return PartyResponse.of(party);
    }

    @Transactional
    public ResponseCode joinParty(int partyId, Account user) {
        Party party = partyRepository.findById(partyId).orElseThrow();
        PartyStatus partyStatus = party.getPartyStatus();

        if (partyStatus != PartyStatus.RECRUIT_OPEN) {
            return ResponseCode.PARTY_NOT_OPEN;
        }

        List<PartyParticipant> partyParticipants = party.getPartyParticipants().stream().filter((participant) -> participant.getParticipantStatus() != ParticipantStatus.CANCELED).toList();
        if (partyParticipants.stream().anyMatch((participant) -> participant.getParticipantStatus() != ParticipantStatus.CANCELED && participant.getAccount().equals(user))) {
            return ResponseCode.PARTY_ALREADY_JOINED;
        }

        if (partyParticipants.size() >= party.getPartyMaxParticipant()) {
            return ResponseCode.PARTY_FULL;
        }

        PartyParticipant partyParticipant = PartyParticipant.builder()
                .party(party)
                .account(user)
                .participantStatus(ParticipantStatus.APPROVED)
                .build();
        partyParticipantRepository.save(partyParticipant);

        return ResponseCode.PARTY_JOIN_SUCCESS;
    }
}
