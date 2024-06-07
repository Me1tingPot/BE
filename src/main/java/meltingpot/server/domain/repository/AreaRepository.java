package meltingpot.server.domain.repository;

import meltingpot.server.domain.entity.Area;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface AreaRepository extends JpaRepository<Area, String> {
    ArrayList<Area> findAllByAreaParentIdNull();
    ArrayList<Area> findAllByAreaParentId(String parentAreaId);

    Area findAreaByAreaParentIdIsNullAndAreaName(String areaName);
    Area findAreaByAreaParentIdAndAreaName(String parentAreaId, String areaName);
}
