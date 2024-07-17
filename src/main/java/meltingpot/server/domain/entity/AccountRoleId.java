package meltingpot.server.domain.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AccountRoleId implements Serializable {

    private Long account;
    private Integer role;

}