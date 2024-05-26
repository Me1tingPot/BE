package meltingpot.server.domain.repository;

import meltingpot.server.domain.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

// post 구현때문에 만들어놓은 것이니 마음대로 수정하셔도 됩니다,,!
public interface AccountRepositroy extends JpaRepository<Account, Long> {
}
