package com.example.srm.model;

import lombok.Data;

/**
 * Represents a user login request payload in the Student Result Management System.
 * This DTO (Data Transfer Object) is used for receiving authentication credentials
 * from client requests to the /api/auth/login endpoint.
 * Uses Lombok's @Data annotation to eliminate boilerplate code.
 */
@Data // Lombok: Generates getters, setters, toString(), equals(), and hashCode()
public class LoginRequest {
    
    /**
     * User's email address serving as the login identifier.
     * This field was renamed from 'loginEmail' to improve clarity and consistency.
     * Format: Valid email address (e.g., "user@institution.edu")
     * Validation: Should be checked for proper email format on the client/server side
     */
    private String email;

    /**
     * User's password for authentication.
     * This field was renamed from 'loginPassword' for simplicity and consistency.
     * Security: Should be transmitted over HTTPS only
     * Storage: Should be hashed in the database (handled by Firebase Auth)
     * Note: Consider adding password complexity requirements in registration
     */
    private String password;
}