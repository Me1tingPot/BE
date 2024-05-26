package meltingpot.server.party.service;

import lombok.RequiredArgsConstructor;
import meltingpot.server.domain.entity.Area;
import meltingpot.server.domain.entity.party.Party;
import meltingpot.server.domain.entity.party.enums.PartyStatus;
import meltingpot.server.domain.entity.party.enums.PartyTemporalFilter;
import meltingpot.server.domain.repository.AreaRepository;
import meltingpot.server.domain.repository.party.PartyRepository;
import meltingpot.server.domain.specification.party.PartySpecification;
import meltingpot.server.party.dto.PartyResponse;
import meltingpot.server.party.dto.PartySearchRequest;
import meltingpot.server.util.PageResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PartySearchService {
    private final PartyRepository partyRepository;
    private final AreaRepository areaRepository;

    @Transactional
    public PageResponse<PartyResponse> searchParty(PartySearchRequest partySearchRequest) {
        String query = partySearchRequest.query();
        if (query.isEmpty()) {
            query = "";
        }

        Specification<Party> spec = Specification.where(PartySpecification.containingAddress(query).or(PartySpecification.containingAddress(query).or(PartySpecification.containingAddress(query))));
        Specification<Party> spec = Specification.where(PartySpecification.containingAddress(query).or(PartySpecification.containingSubject(query).or(PartySpecification.containingOwner(query))));

        if (partySearchRequest.areaIdFilter() != null) {
            ArrayList<Area> targetAreas = new ArrayList<>();
            Area currentArea = areaRepository.findById(partySearchRequest.areaIdFilter()).orElseThrow();
            while (currentArea != null) {
                targetAreas.add(currentArea);
                currentArea = currentArea.getAreaParent();
            }

            spec = spec.and(PartySpecification.inArea(targetAreas));
        }

        if (partySearchRequest.temporalFilter() != null) {
            List<PartyTemporalFilter> partyTemporalFilter = new ArrayList<>();
            for (String temporalFilter : partySearchRequest.temporalFilter()) {
                partyTemporalFilter.add(PartyTemporalFilter.valueOf(temporalFilter));
            }

            if (partyTemporalFilter.contains(PartyTemporalFilter.RECENT_MONTH)) {
                spec = spec.and(PartySpecification.startInDay(7L));
            } else if (partyTemporalFilter.contains(PartyTemporalFilter.RECENT_WEEK)) {
                spec = spec.and(PartySpecification.startInDay(1L));
            }

            if (partyTemporalFilter.contains(PartyTemporalFilter.ORDER_CREATED_AT_ASC)) {
                spec = spec.and(PartySpecification.orderByStartTimeAsc());
            } else {
                spec = spec.and(PartySpecification.orderByStartTimeDesc());
            }
        } else {
            spec = spec.and(PartySpecification.orderByStartTimeDesc());
        }

        if (partySearchRequest.statusFilter() != null) {
            PartyStatus partyStatus = PartyStatus.valueOf(partySearchRequest.statusFilter());

            if (partyStatus.equals(PartyStatus.RECRUIT_CLOSED)) {
                spec = spec.and(PartySpecification.isStatus(PartyStatus.RECRUIT_CLOSED));
            } else if (partyStatus.equals(PartyStatus.RECRUIT_SCHEDULED)) {
                spec = spec.and(PartySpecification.isStatus(PartyStatus.RECRUIT_SCHEDULED));
            } else {
                spec = spec.and(PartySpecification.isStatus(PartyStatus.RECRUIT_OPEN));
            }
        } else {
            spec = spec.and(PartySpecification.isStatus(PartyStatus.RECRUIT_OPEN));
        }

        // 전체 개수 카운트
        int totalCount = (int) partyRepository.count(spec);

        // 페이징 처리
        int page = 1;
        int itemsPerPage = 30;
        if (partySearchRequest.page() != null) {
            page = partySearchRequest.page();
        }

        int totalPage = (int) Math.ceil((double) totalCount / itemsPerPage);

        List<PartyResponse> partyResponses = partyRepository.findAll(spec, PageRequest.of(page - 1, itemsPerPage)).stream().map(PartyResponse::of).toList();
        return new PageResponse<>(partyResponses, page, itemsPerPage, totalPage, totalCount);
    }
}
