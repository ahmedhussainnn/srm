package com.example.srm.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Represents a student's academic result for a specific course in the Student Result Management System.
 * This entity maps to a document in the 'results' collection in Firestore.
 * Uses Lombok annotations to eliminate boilerplate code for getters, setters, and constructors.
 */
@Data                       // Lombok: Generates getters, setters, equals(), hashCode(), and toString()
@NoArgsConstructor          // Lombok: Generates a no-argument constructor
@AllArgsConstructor         // Lombok: Generates a constructor with all fields as arguments
public class Result {

    /**
     * Auto-generated Firestore document ID that uniquely identifies this result record.
     * This ID is automatically assigned by Firestore when the document is created.
     */
    private String id;

    /**
     * The student's unique roll number associated with this result.
     * This creates a relationship to the Student entity.
     * Format: Institution-specific student identifier (e.g., "SRM001")
     */
    private String rollNumber;

    /**
     * The course code for which this result is recorded.
     * This creates a relationship to the Course entity.
     * Format: Standard course code format (e.g., "CS101", "MATH201")
     */
    private String courseCode;

    /**
     * The numerical marks obtained by the student in this course.
     * Range: Typically 0-100, but depends on grading system.
     * Note: Consider adding validation for mark ranges.
     */
    private int marks;

    /**
     * The grade derived from the marks according to the institution's grading system.
     * Examples: "A", "B+", "F", "P" (Pass), etc.
     * Note: Consider using an enum for standardized grade values.
     */
    private String grade;
}