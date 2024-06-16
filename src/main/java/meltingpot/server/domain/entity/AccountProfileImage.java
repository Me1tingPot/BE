package meltingpot.server.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import meltingpot.server.domain.entity.common.BaseEntity;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AccountProfileImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @NotNull
    private String imageKey;

    @NotNull
    private boolean isThumbnail;

    @NotNull
    private String imageOriginalName;

    @Column(nullable = true)
    private int sequence; // 네 장 중 몇 번째

}