package com.example.srm.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Configuration class for initializing Firebase Admin SDK.
 * This class handles the setup of Firebase services using service account credentials.
 * The initialization happens automatically when the Spring application starts.
 */
@Configuration // Marks this class as a Spring configuration component
public class FirebaseConfig {

    // Logger for tracking initialization events and errors
    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    /**
     * Path to the Firebase service account key JSON file.
     * Injected from application.properties using Spring's @Value.
     * Example property: firebase.service-account.key-path=classpath:firebase-service-account.json
     */
    @Value("${firebase.service-account.key-path}")
    private Resource serviceAccountKeyResource;

    /**
     * Initializes Firebase Admin SDK during application startup.
     * This method runs automatically after dependency injection is complete.
     * 
     * The initialization process:
     * 1. Checks if Firebase is already initialized
     * 2. Loads the service account credentials
     * 3. Configures FirebaseOptions
     * 4. Initializes the FirebaseApp
     * 
     * @throws RuntimeException if initialization fails, which will prevent application startup
     */
    @PostConstruct // Executes after construction and dependency injection
    public void initializeFirebase() {
        try {
            // Check for existing Firebase instances to avoid duplicate initialization
            if (FirebaseApp.getApps().isEmpty()) {
                InputStream serviceAccountStream = null;
                String credentialSource = "";

                // Attempt to load credentials from classpath resource
                if (serviceAccountKeyResource != null && serviceAccountKeyResource.exists()) {
                    logger.info("Loading Firebase credentials from resource: {}", 
                             serviceAccountKeyResource.getDescription());
                    serviceAccountStream = serviceAccountKeyResource.getInputStream();
                    credentialSource = "classpath resource (" + serviceAccountKeyResource.getFilename() + ")";
                } else {
                    logger.error("Firebase credentials file not found. Configure 'firebase.service-account.key-path'");
                    throw new IOException("Firebase credentials file could not be loaded.");
                }

                // Build Firebase configuration options
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                        // Additional Firebase services can be configured here:
                        // .setDatabaseUrl("https://<PROJECT_ID>.firebaseio.com") // For Realtime Database
                        // .setStorageBucket("<BUCKET_NAME>.appspot.com") // For Cloud Storage
                        .build();

                // Initialize the default Firebase app
                FirebaseApp.initializeApp(options);
                logger.info("Firebase Admin SDK initialized successfully using {}", credentialSource);

                // Ensure the input stream is closed
                if (serviceAccountStream != null) {
                    serviceAccountStream.close();
                }
            } else {
                logger.info("Firebase Admin SDK already initialized - skipping reinitialization");
            }
        } catch (IOException e) {
            logger.error("CRITICAL: Failed to initialize Firebase Admin SDK", e);
            // Fail fast - if Firebase can't initialize, the application shouldn't start
            throw new RuntimeException("Failed to initialize Firebase Admin SDK", e);
        }
    }
}