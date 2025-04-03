package com.example.srm.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Represents a Lecturer entity in the Student Result Management System.
 * This class models lecturer information and maps to a Firestore document
 * in the 'lecturers' collection.
 * Uses Lombok annotations to minimize boilerplate code.
 */
@Data                       // Lombok: Generates getters, setters, equals(), hashCode(), and toString()
@NoArgsConstructor          // Lombok: Generates a no-argument constructor
@AllArgsConstructor         // Lombok: Generates a constructor with all arguments
public class Lecturer {

    /**
     * Auto-generated Firestore document ID that uniquely identifies this lecturer.
     * This is the primary key in the Firestore collection.
     * Note: Consider whether this can serve as the lecturerId or if a separate
     * identifier is needed for business logic purposes.
     */
    private String id;

    /**
     * Institution-specific lecturer identifier (e.g., employee ID).
     * This may be used for official records or as a display ID.
     * Design consideration: Could potentially be merged with 'id' field
     * depending on Firestore structure and application requirements.
     */
    private String lecturerId;

    /**
     * Full name of the lecturer (e.g., "Dr. Sarah Johnson").
     * Used for display purposes throughout the application.
     */
    private String lecturerName;

    /**
     * Official email address of the lecturer.
     * Used for communication and potentially as a login identifier.
     * Should follow standard email format (user@domain.com).
     */
    private String lecturerEmail;
}