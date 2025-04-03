package com.example.srm.model;

import lombok.Data;

/**
 * Represents a user registration request payload in the Student Result Management System.
 * This DTO (Data Transfer Object) carries the necessary credentials for creating new user accounts.
 * Used by the /api/auth/signup endpoint to process new registrations.
 */
@Data // Lombok: Generates getters, setters, toString(), equals(), and hashCode()
public class SignUpRequest {

    /**
     * The email address to be used as the user's primary identifier.
     * Renamed from 'signupEmail' to maintain consistency with LoginRequest.
     * 
     * Validation Requirements:
     * - Must follow standard email format (user@domain.tld)
     * - Should be unique across the system
     * - Should be verified via confirmation email in production
     * 
     * Example: "student.name@university.edu"
     */
    private String email;

    /**
     * The password for the new user account.
     * Renamed from 'signupPassword' for naming consistency.
     * 
     * Security Requirements:
     * - Will be transmitted over HTTPS only
     * - Minimum length enforcement (typically 8+ characters)
     * - Should contain mix of character types in production
     * - Will be hashed by Firebase Auth before storage
     * 
     */
    private String password;
}