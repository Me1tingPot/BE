package meltingpot.server.util;

import java.util.stream.Collectors;
import meltingpot.server.domain.entity.Account;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class AccountUser extends User {
    public AccountUser(Account account) {
        super(account.getUsername(), account.getPassword(),
                account.toAuthStringList().stream().map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList()));
    }
}