package meltingpot.server.domain.entity;


import jakarta.persistence.*;
import lombok.*;
import meltingpot.server.domain.entity.common.BaseEntity;
import meltingpot.server.domain.entity.enums.Type;

import java.util.ArrayList;
import java.util.List;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class Community extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "community_id")
    private Long id;

    private String title;

    private String content;

    @Enumerated(EnumType.STRING)
    private Type type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "community")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "community")
    private List<CommunityImage> communityImages = new ArrayList<>();

    @OneToMany(mappedBy = "community")
    private List<Report> reports = new ArrayList<>();

}
