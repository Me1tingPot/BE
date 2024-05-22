package meltingpot.server.domain.entity;


import jakarta.persistence.*;
import lombok.*;
import meltingpot.server.domain.entity.common.BaseEntity;
import java.util.ArrayList;
import java.util.List;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    private String title;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<Image> Images = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<Report> reports = new ArrayList<>();

}
