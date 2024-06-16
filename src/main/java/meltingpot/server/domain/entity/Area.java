package meltingpot.server.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import meltingpot.server.domain.entity.common.BaseEntity;

import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Area extends BaseEntity {
    @Id
    @Column(columnDefinition = "CHAR(10)")
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "area_parent_id")
    private Area areaParent;

    @OneToMany(mappedBy = "areaParent", fetch = FetchType.LAZY)
    private List<Area> subAreas;

    @NotNull
    private String areaName;
}