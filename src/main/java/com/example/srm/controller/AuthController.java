package com.example.srm.controller;

import com.example.srm.model.LoginRequest;
import com.example.srm.model.SignUpRequest;
import com.example.srm.service.AuthService;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for handling user authentication (login/signup) via Firebase.
 * Endpoints are prefixed with `/api/auth` for API clarity.
 * Note: Contains hardcoded credentials for demo purposes (replace with proper role-based auth in production).
 */
@RestController
@RequestMapping("/api/auth") // Using /api prefix for clarity
public class AuthController {

    // Logger for tracking authentication events and errors
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    // Service layer for Firebase authentication logic
    private final AuthService authService;

    // ==== HARDCODED CREDENTIALS (FOR DEMO ONLY - INSECURE!) ====
    // proper role-based authentication (e.g., JWT claims or Firebase Custom Claims).
    private static final String STUDENT_EMAIL = "student@gmail.com";
    private static final String STUDENT_PASSWORD = "student";
    private static final String LECTURER_EMAIL = "lecturer@gmail.com";
    private static final String LECTURER_PASSWORD = "lecturer";

    // Constructor injection for AuthService (Spring best practice)
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Handles user login requests.
     * First checks against hardcoded credentials (demo), then falls back to Firebase.
     * 
     * @param loginRequest Contains email and password from the client.
     * @return ResponseEntity with success message + role (for hardcoded users) 
     *         or Firebase auth tokens (for other users).
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // --- Start Hardcoded User Check (Not Secure) ---
        // This block is for demo compatibility only. In production will use Firebase roles/JWT claims.
        if (STUDENT_EMAIL.equals(loginRequest.getEmail())) {
            if (STUDENT_PASSWORD.equals(loginRequest.getPassword())) {
                logger.info("Successful hardcoded login for STUDENT: {}", loginRequest.getEmail());
                // In a real app, generate a JWT or session for this user
                return ResponseEntity.ok(Map.of(
                    "message", "Student login successful (Hardcoded)", 
                    "role", "student", 
                    "email", loginRequest.getEmail()
                ));
            } else {
                logger.warn("Failed hardcoded login attempt for STUDENT (wrong password): {}", loginRequest.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials"));
            }
        }
        if (LECTURER_EMAIL.equals(loginRequest.getEmail())) {
            if (LECTURER_PASSWORD.equals(loginRequest.getPassword())) {
                logger.info("Successful hardcoded login for LECTURER: {}", loginRequest.getEmail());
                return ResponseEntity.ok(Map.of(
                    "message", "Lecturer login successful (Hardcoded)", 
                    "role", "lecturer", 
                    "email", loginRequest.getEmail()
                ));
            } else {
                logger.warn("Failed hardcoded login attempt for LECTURER (wrong password): {}", loginRequest.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials"));
            }
        }
        // --- End Hardcoded User Check ---

        // --- Attempt Firebase Sign-in for non-hardcoded users ---
        logger.info("Attempting Firebase sign-in for non-hardcoded user: {}", loginRequest.getEmail());
        try {
            // Delegate to AuthService for Firebase authentication
            Map<String, Object> firebaseResponse = authService.signInWithPassword(loginRequest);
            // Return raw Firebase response (consider mapping to a DTO for client safety)
            return ResponseEntity.ok(firebaseResponse);
        } catch (RuntimeException e) {
            // Handle AuthService errors (e.g., invalid credentials)
            String message = e.getMessage() != null ? e.getMessage() : "Invalid credentials or user not found.";
            if (message.contains("INVALID_LOGIN_CREDENTIALS") || message.contains("INVALID_PASSWORD") || message.contains("EMAIL_NOT_FOUND")) {
                message = "Invalid email or password.";
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", message));
        } catch (Exception e) {
            // Catch-all for unexpected errors (e.g., network issues)
            logger.error("Unexpected internal server error during login for {}: {}", loginRequest.getEmail(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An internal server error occurred during login."));
        }
    }

    /**
     * Handles user registration (signup) via Firebase Admin SDK.
     * 
     * @param signUpRequest Contains email, password, and optional metadata.
     * @return ResponseEntity with Firebase UID and email on success, or error message.
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignUpRequest signUpRequest) {
        // Basic validation (replace with @Valid and validation annotations if needed)
        if (signUpRequest.getEmail() == null || signUpRequest.getEmail().isEmpty() ||
            signUpRequest.getPassword() == null || signUpRequest.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email and password are required."));
        }
        // Add more robust validation (e.g., email format, password strength) here if needed

        logger.info("Attempting Firebase sign-up for {}", signUpRequest.getEmail());
        try {
            // Delegate to AuthService for Firebase user creation
            UserRecord userRecord = authService.signUp(signUpRequest);
            logger.info("Successfully created Firebase user via Admin SDK: UID={}, Email={}", userRecord.getUid(), userRecord.getEmail());
            
            // Return sanitized user data (exclude sensitive fields like passwordHash)
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Signup successful!",
                "uid", userRecord.getUid(),
                "email", userRecord.getEmail()
            ));
        } catch (FirebaseAuthException e) {
            // Handle Firebase-specific errors (e.g., duplicate email)
            logger.error("Firebase signup failed for {}: {}", signUpRequest.getEmail(), e.getMessage(), e);
            String errorMessage = "Signup failed: " + e.getMessage();
            
            // Map Firebase error codes to user-friendly messages
            String authErrorCode = e.getAuthErrorCode() != null ? e.getAuthErrorCode().name() : "";
            if ("EMAIL_ALREADY_EXISTS".equals(authErrorCode)) {
                errorMessage = "Signup failed: Email already in use.";
            } else if ("INVALID_EMAIL".equals(authErrorCode)) {
                errorMessage = "Signup failed: Invalid email format.";
            }
            // Add more conditions as needed
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", errorMessage));
        } catch (Exception e) {
            // Catch-all for other exceptions (e.g., network issues)
            logger.error("Unexpected internal server error during signup for {}: {}", signUpRequest.getEmail(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An internal error occurred during signup."));
        }
    }
}