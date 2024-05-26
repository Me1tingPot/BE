package meltingpot.server.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import lombok.experimental.SuperBuilder;
import jakarta.validation.constraints.NotNull;
import meltingpot.server.domain.entity.common.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Entity
public class Account extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    // 스프링 시큐리티에서 사용할 '이메일'
    @NotNull
    @Column(unique = true)
    private String username;

    // 실제 사용자 이름
    @NotNull
    private String name;

    @NotNull
    private String password;

    @NotNull
    private enum gender{ male, female, unknown };

    @NotNull
    private LocalDate birth;

    @NotNull
    private String nationality;

    @NotNull
    private String language;

    @NotNull
    private String country;

    @NotNull
    private String city;

    @NotNull
    private LocalDateTime created;

    @NotNull
    private LocalDateTime updated;

    private LocalDateTime deleted;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL )
    private List<AccountProfileImage> profileImages = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "account")
    private List<AccountRole> accountRoles = new ArrayList<>();

    public List<String> toAuthStringList() {
        return accountRoles.stream().map(a -> a.getRole().getAuthority())
                .collect(Collectors.toList());
    }

}