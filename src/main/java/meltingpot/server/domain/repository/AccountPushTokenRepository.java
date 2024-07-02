package meltingpot.server.domain.repository;

import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.AccountPushToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountPushTokenRepository extends JpaRepository<AccountPushToken, Long> {
    List<AccountPushToken> findAllByAccount(Account account);
    Boolean existsAccountPushByAccountAndToken(Account account, String token);
}
