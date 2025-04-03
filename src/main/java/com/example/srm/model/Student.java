package com.example.srm.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Represents a Student entity in the Student Result Management System.
 * This class maps to a document in the 'students' collection in Firestore.
 * Uses Lombok annotations to automatically generate boilerplate code.
 */
@Data                       // Lombok: Generates getters, setters, equals(), hashCode(), and toString()
@NoArgsConstructor          // Lombok: Generates a no-argument constructor
@AllArgsConstructor         // Lombok: Generates a constructor with all fields as arguments
public class Student {

    /**
     * Auto-generated Firestore document ID that uniquely identifies this student record.
     * This ID is automatically assigned by Firestore when the document is created.
     * Format: Firestore-generated unique identifier
     */
    private String id;

    /**
     * Institution-assigned unique roll number for the student.
     * This serves as the primary business identifier for students.
     * Format: Institution-specific format (e.g., "SRM20230001")
     * Note: Should be unique across all students
     */
    private String rollNumber;

    /**
     * Full name of the student.
     * Format: First name followed by last name (e.g., "John Doe")
     */
    private String name;

    /**
     * Official email address of the student.
     * Used for communication and potentially as a login identifier.
     * Format: Standard email format (e.g., "john.doe@university.edu")
     * Note: Should be validated for proper email format
     */
    private String email;
}