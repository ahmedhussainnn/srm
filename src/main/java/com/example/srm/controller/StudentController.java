package com.example.srm.controller;

import com.example.srm.model.Student;
import com.example.srm.service.FirestoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * REST controller for managing student-related operations.
 * Provides CRUD endpoints for student records in Firestore.
 * All endpoints are prefixed with '/api/students'.
 */
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);
    private final FirestoreService firestoreService;

    /**
     * Constructor for dependency injection of FirestoreService.
     * @param firestoreService The service layer for Firestore operations
     */
    public StudentController(FirestoreService firestoreService) {
        this.firestoreService = firestoreService;
    }

    /**
     * Creates a new student record.
     * @param student The student object to be created (from request body)
     * @return ResponseEntity containing the created student or error message
     */
    @PostMapping
    public ResponseEntity<?> addStudent(@RequestBody Student student) {
        try {
            // Validate required fields
            if (student.getRollNumber() == null || student.getRollNumber().isEmpty() ||
                student.getName() == null || student.getName().isEmpty() ||
                student.getEmail() == null || student.getEmail().isEmpty()) {
                logger.warn("Add student request failed validation: {}", student);
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Roll number, name, and email are required."
                ));
            }

            Student createdStudent = firestoreService.addStudent(student);
            logger.info("Student added successfully: ID={}, Roll={}", 
                createdStudent.getId(), createdStudent.getRollNumber());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdStudent);
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Error adding student (Roll={}): {}", 
                student.getRollNumber(), e.getMessage(), e);
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to add student. Please try again later."));
        } catch (Exception e) {
            logger.error("Unexpected error adding student (Roll={}): {}", 
                student.getRollNumber(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Retrieves all student records.
     * @return ResponseEntity containing list of students or error message
     */
    @GetMapping
    public ResponseEntity<?> getAllStudents() {
        try {
            List<Student> students = firestoreService.getAllStudents();
            logger.debug("Retrieved {} students.", students.size());
            return ResponseEntity.ok(students);
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Error retrieving students: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve students. Please try again later."));
        } catch (Exception e) {
            logger.error("Unexpected error retrieving students: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Retrieves a specific student by ID.
     * @param id The ID of the student to retrieve
     * @return ResponseEntity containing the student or error message
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable String id) {
        try {
            Student student = firestoreService.getStudent(id);
            if (student != null) {
                logger.debug("Retrieved student by ID: {}", id);
                return ResponseEntity.ok(student);
            } else {
                logger.warn("Student not found with ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Student not found with ID: " + id));
            }
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Error retrieving student {}: {}", id, e.getMessage(), e);
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve student. Please try again later."));
        } catch (Exception e) {
            logger.error("Unexpected error retrieving student {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Updates a student record.
     * @param id The ID of the student to update
     * @param updatedData Map containing fields to update
     * @return ResponseEntity with success/error message
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable String id, 
                                         @RequestBody Map<String, Object> updatedData) {
        if (updatedData == null || updatedData.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Update data cannot be empty."));
        }

        try {
            firestoreService.updateStudent(id, updatedData);
            logger.info("Student updated successfully: {}", id);
            return ResponseEntity.ok(Map.of(
                "message", "Student updated successfully", 
                "id", id
            ));
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Error updating student {}: {}", id, e.getMessage(), e);
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to update student. Please try again later."));
        } catch (Exception e) {
            logger.error("Unexpected error updating student {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred during update."));
        }
    }

    /**
     * Deletes a student record.
     * @param id The ID of the student to delete
     * @return ResponseEntity with success/error message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable String id) {
        try {
            firestoreService.deleteStudent(id);
            logger.info("Student deleted successfully: {}", id);
            return ResponseEntity.noContent().build(); // HTTP 204
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Error deleting student {}: {}", id, e.getMessage(), e);
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to delete student. Please try again later."));
        } catch (Exception e) {
            logger.error("Unexpected error deleting student {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred during deletion."));
        }
    }
}