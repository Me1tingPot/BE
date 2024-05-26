package meltingpot.server.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import meltingpot.server.domain.entity.common.BaseEntity;
import meltingpot.server.domain.entity.enums.Gender;
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
    @NotNull
    @Enumerated(EnumType.STRING)
    private Gender gender;

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
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL )
    private List<AccountProfileImage> profileImages = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "account")
    private List<AccountRole> accountRoles = new ArrayList<>();

    public List<String> toAuthStringList() {
        return accountRoles.stream().map(a -> a.getRole().getAuthority())
                .collect(Collectors.toList());
    }
  
    @OneToMany(mappedBy = "account")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "account")
    private List<Post> posts = new ArrayList<>();

}
