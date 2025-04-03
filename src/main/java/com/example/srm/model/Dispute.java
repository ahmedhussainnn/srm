package com.example.srm.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Represents a grade dispute raised by a student in the Student Result Management System.
 * Maps to a Firestore document in the 'disputes' collection.
 * Uses Lombok annotations to automatically generate boilerplate code.
 */
@Data                       // Lombok: Generates getters, setters, equals(), hashCode(), and toString()
@NoArgsConstructor          // Lombok: Generates a no-argument constructor
@AllArgsConstructor         // Lombok: Generates a constructor with all fields as arguments
public class Dispute {

    /**
     * Auto-generated Firestore document ID that uniquely identifies this dispute.
     * This is automatically assigned by Firestore when the document is created.
     */
    private String id;

    /**
     * The roll number of the student raising the dispute.
     * This serves as a reference to the student involved in the dispute.
     */
    private String rollNumber;

    /**
     * The course code (e.g., "CS101") for which the dispute is being raised.
     * References the specific course where the grade dispute occurs.
     */
    private String courseCode;

    /**
     * Detailed explanation from the student about why they are disputing their grade.
     * This should contain specific reasons for the dispute (e.g., grading error, missing assignment).
     */
    private String reason;

    /**
     * Current status of the dispute resolution process.
     * Typical values include:
     * - "pending": Initial state when dispute is first submitted
     * - "resolved": When dispute has been settled satisfactorily
     * - "rejected": When dispute has been reviewed and denied
     * Consider using an enum in future versions for type safety.
     */
    private String status;
}