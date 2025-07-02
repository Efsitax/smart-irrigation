package com.kadir.smartirrigation.common.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Bean
    @SneakyThrows
    public FirebaseApp firebaseApp() {
        try (InputStream serviceAccount =
                     getClass().getClassLoader()
                             .getResourceAsStream("firebase/serviceAccountKey.json")) {

            if (serviceAccount == null) {
                throw new IllegalStateException(
                        "serviceAccountKey.json not found! Check the source path.");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            return FirebaseApp.initializeApp(options);
        }
    }
}