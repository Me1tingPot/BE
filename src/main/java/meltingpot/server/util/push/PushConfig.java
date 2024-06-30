package meltingpot.server.util.push;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;

@Configuration
public class PushConfig {
    @Value("${cloud.firebase.config}")
    private String firebaseConfig;

    @Bean
    public FirebaseApp firebaseApp() {
        try {
            FileInputStream serviceAccount =
                    new FileInputStream(firebaseConfig);

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            return FirebaseApp.initializeApp(options);
        } catch (Exception e) {
            return null;
        }
    }
}
