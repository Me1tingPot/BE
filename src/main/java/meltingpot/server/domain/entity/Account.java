package meltingpot.server.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @NotNull
    @Column(unique = true)
    private String username;

    @NotNull
    @Column(unique = true)
    private String email;

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

}
