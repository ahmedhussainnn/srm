package com.example.srm.service;

import com.example.srm.model.LoginRequest;
import com.example.srm.model.SignUpRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Value("${firebase.rest.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    // Inject RestTemplate
    public AuthService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Using Firebase Admin SDK for Sign Up
    public UserRecord signUp(SignUpRequest signUpRequest) throws FirebaseAuthException {
        logger.info("Attempting Firebase Admin SDK sign-up for email: {}", signUpRequest.getEmail());
         UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(signUpRequest.getEmail())
                .setEmailVerified(false) // Or true, depending on your flow
                .setPassword(signUpRequest.getPassword())
                .setDisabled(false);

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
        logger.info("Firebase Admin SDK sign-up successful for UID: {}, Email: {}", userRecord.getUid(), userRecord.getEmail());
        return userRecord;
    }


    // Replicating the REST API call for Sign In from auth_lib.py
    public Map<String, Object> signInWithPassword(LoginRequest loginRequest) {
        logger.info("Attempting Firebase REST API sign-in for email: {}", loginRequest.getEmail());
        final String uri = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Map<String, Object> map = new HashMap<>();
        map.put("email", loginRequest.getEmail());
        map.put("password", loginRequest.getPassword());
        map.put("returnSecureToken", true);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(uri, entity, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                @SuppressWarnings("unchecked") // Suppress warning for cast
                Map<String, Object> responseBody = response.getBody();
                logger.info("Firebase REST API sign-in successful for email: {}", loginRequest.getEmail());
                // You might want to sanitize the response before returning,
                // e.g., remove refresh token if not needed by the caller.
                return responseBody; // Contains idToken, refreshToken, etc.
            } else {
                // Log the status code even if body is null
                logger.error("Firebase REST API sign-in failed with status: {}", response.getStatusCode());
                 String errorMsg = "Sign in failed: Status Code " + response.getStatusCode();
                 if (response.getBody() != null) {
                    errorMsg += " Body: " + response.getBody();
                 }
                 throw new RuntimeException(errorMsg);
            }
        } catch (HttpClientErrorException e) {
             // Attempt to parse the error message from Firebase response body
            String firebaseErrorMsg = e.getResponseBodyAsString();
            logger.error("Firebase REST API sign-in request failed for email {}. Status: {}. Response: {}",
                         loginRequest.getEmail(), e.getStatusCode(), firebaseErrorMsg, e);
            // Extract a user-friendly message if possible, otherwise use the raw response
            String errorMessage = "Sign in failed: " + firebaseErrorMsg;
             // Example parsing (adjust based on actual Firebase error structure):
             // try {
             //     ObjectMapper mapper = new ObjectMapper();
             //     JsonNode root = mapper.readTree(firebaseErrorMsg);
             //     errorMessage = root.path("error").path("message").asText("Sign in failed.");
             // } catch (Exception parseEx) { /* Ignore parsing error, use raw message */ }

            throw new RuntimeException(errorMessage, e); // Include original exception
        }
         catch (Exception e) {
            // Catch other potential exceptions (network issues, etc.)
            logger.error("An unexpected error occurred during REST API sign-in for email {}: {}", loginRequest.getEmail(), e.getMessage(), e);
            throw new RuntimeException("An unexpected error occurred during sign in.", e);
        }
    }
}
