package meltingpot.server.util.push;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import meltingpot.server.domain.entity.Account;
import meltingpot.server.domain.entity.AccountPushToken;
import meltingpot.server.domain.repository.AccountPushTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PushService {
    private final AccountPushTokenRepository accountPushTokenRepository;
    private final FirebaseApp firebaseApp;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void sendPush(Account account, String title, String body) {
        List<AccountPushToken> tokens = accountPushTokenRepository.findAllByAccount(account);

        FirebaseMessaging messaging = FirebaseMessaging.getInstance(firebaseApp);
        for (AccountPushToken token : tokens) {
            Message message = Message.builder()
                    .setToken(token.getToken())
                    .putData("title", title)
                    .putData("body", body)
                    .build();

            try {
                messaging.send(message);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }
}
