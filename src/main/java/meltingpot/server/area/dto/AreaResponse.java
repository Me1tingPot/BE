package meltingpot.server.area.dto;

import meltingpot.server.domain.entity.Area;

public record AreaResponse(
    String areaId,
    String areaName
) {
    public static AreaResponse of(Area area) {
        return new AreaResponse(
            area.getId(),
            area.getAreaName()
        );
    }
}
