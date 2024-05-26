package meltingpot.server.party.service;

import lombok.RequiredArgsConstructor;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.party.*;
import meltingpot.server.domain.entity.party.enums.ParticipantStatus;
import meltingpot.server.domain.entity.party.enums.PartyStatus;
import meltingpot.server.domain.repository.AreaRepository;
import meltingpot.server.domain.repository.party.PartyParticipantRepository;
import meltingpot.server.domain.repository.party.PartyReportRepository;
import meltingpot.server.domain.repository.party.PartyRepository;
import meltingpot.server.party.dto.PartyCreateRequest;
import meltingpot.server.party.dto.PartyReportRequest;
import meltingpot.server.party.dto.PartyResponse;
import meltingpot.server.util.ResponseCode;
import meltingpot.server.util.r2.FileService;
import meltingpot.server.util.r2.FileUploadResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PartyService {
    private final PartyRepository partyRepository;
    private final PartyParticipantRepository partyParticipantRepository;
    private final PartyReportRepository partyReportRepository;

    private final FileService fileService;
    private final AreaRepository areaRepository;

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

    @Transactional
    public ResponseCode reportParty(int partyId, Account user, PartyReportRequest partyReportRequest) {
        Party party = partyRepository.findById(partyId).orElseThrow();
        PartyStatus partyStatus = party.getPartyStatus();

        if (party.getAccount().equals(user)) {
            return ResponseCode.PARTY_REPORT_SELF;
        }

        if (partyStatus == PartyStatus.CANCELED || partyStatus == PartyStatus.TEMP_SAVED) {
            return ResponseCode.PARTY_NOT_OPEN;
        }

        if (party.getPartyParticipants().stream().anyMatch((participant) -> participant.getAccount().equals(user))) {
            return ResponseCode.PARTY_REPORT_ALREADY;
        }

        PartyReport partyReport = PartyReport.builder()
                .party(party)
                .account(user)
                .reportContent(partyReportRequest.reportContent())
                .build();
        partyReportRepository.save(partyReport);

        return ResponseCode.PARTY_REPORT_SUCCESS;
    }

    @Transactional
    public FileUploadResponse generateImageUploadUrl() {
        return fileService.getPreSignedUrl("party-image");
    }

    @Transactional
    public ResponseCode createParty(Account user, PartyCreateRequest partyCreateRequest) {
        TemporalAccessor ta = DateTimeFormatter.ISO_INSTANT.parse(partyCreateRequest.startTime());
        Instant i = Instant.from(ta);
        LocalDateTime startTime = LocalDateTime.ofInstant(i, ZoneId.systemDefault());

        PartyStatus partyStatus = PartyStatus.RECRUIT_OPEN;
        if (partyCreateRequest.isTempSave()) {
            Party existingTempSaved = partyRepository.findByAccountAndPartyStatus(user, PartyStatus.TEMP_SAVED);
            if (existingTempSaved != null) {
                existingTempSaved.setPartyStatus(PartyStatus.CANCELED);
                partyRepository.save(existingTempSaved);
            }

            partyStatus = PartyStatus.TEMP_SAVED;
        }

        Party party = Party.builder()
                .account(user)
                .partySubject(partyCreateRequest.subject())
                .partyLocationAddress(partyCreateRequest.locationAddress())
                .partyLocationDetail(partyCreateRequest.locationDetail())
                .partyStartTime(startTime)
                .partyArea(areaRepository.findById(partyCreateRequest.areaId()).orElseThrow())
                .partyMinParticipant(partyCreateRequest.partyMinParticipant())
                .partyMaxParticipant(partyCreateRequest.partyMaxParticipant())
                .partyLocationReserved(partyCreateRequest.locationIsReserved())
                .partyLocationCanBeChanged(partyCreateRequest.locationCanBeChanged())
                .partyStatus(partyStatus)
                .build();
        party.setPartyImages(partyCreateRequest.imageKey().stream().map((key) -> PartyImage.builder().partyImageUploader(user).party(party).partyImageKey(key).partyImageOriginalName("").build()).toList());
        party.setPartyContents(List.of(PartyContent.builder().party(party).partyContentLang(partyCreateRequest.descriptionLanguage()).partyContent(partyCreateRequest.description()).build()));

        partyRepository.save(party);

        return ResponseCode.PARTY_CREATE_SUCCESS;
    }

    @Transactional
    public PartyResponse getTempSavedParty(Account user) {
        Party party = partyRepository.findByAccountAndPartyStatus(user, PartyStatus.TEMP_SAVED);
        if (party == null) {
            throw new NoSuchElementException();
        }

        party.setPartyStatus(PartyStatus.CANCELED);
        partyRepository.save(party);

        return PartyResponse.of(party);
    }
}
