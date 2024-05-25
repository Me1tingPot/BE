package meltingpot.server.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserProfileImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String imageKey;

    @NotNull
    private LocalDateTime created;

    @NotNull
    private LocalDateTime updated;

    private LocalDateTime deleted;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}