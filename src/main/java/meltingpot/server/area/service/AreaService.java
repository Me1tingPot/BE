package meltingpot.server.area.service;

import lombok.RequiredArgsConstructor;
import meltingpot.server.area.dto.AreaResponse;
import meltingpot.server.domain.repository.AreaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AreaService {
    private static final Logger log = LoggerFactory.getLogger(AreaService.class);
    private final AreaRepository areaRepository;

    @Transactional
    public List<AreaResponse> listArea(Integer parentAreaId) {
        if (parentAreaId == null) {
            return areaRepository.findAllByAreaParentIdNull().stream()
                    .map(AreaResponse::of)
                    .collect(Collectors.toList());
        } else {
            return areaRepository.findAllByAreaParentId(parentAreaId).stream()
                    .map(AreaResponse::of)
                    .collect(Collectors.toList());
        }
    }
}
