package com.example.srm.controller;

import com.example.srm.model.Lecturer;
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
 * REST controller for managing lecturer-related operations.
 * Provides CRUD endpoints for lecturer records in Firestore.
 * All endpoints are prefixed with '/api/lecturers'.
 */
@RestController
@RequestMapping("/api/lecturers")
public class LecturerController {

    private static final Logger logger = LoggerFactory.getLogger(LecturerController.class);
    private final FirestoreService firestoreService;

    /**
     * Constructor for dependency injection of FirestoreService.
     * @param firestoreService The service layer for Firestore operations
     */
    public LecturerController(FirestoreService firestoreService) {
        this.firestoreService = firestoreService;
    }

    /**
     * Creates a new lecturer record.
     * @param lecturer The lecturer object to be created (from request body)
     * @return ResponseEntity containing the created lecturer or error message
     */
    @PostMapping
    public ResponseEntity<?> addLecturer(@RequestBody Lecturer lecturer) {
        try {
            // Validate required fields
            if (lecturer.getLecturerId() == null || lecturer.getLecturerId().isEmpty() ||
                lecturer.getLecturerName() == null || lecturer.getLecturerName().isEmpty() ||
                lecturer.getLecturerEmail() == null || lecturer.getLecturerEmail().isEmpty()) {
                logger.warn("Add lecturer request failed validation: {}", lecturer);
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Lecturer ID, name, and email are required."
                ));
            }

            Lecturer createdLecturer = firestoreService.addLecturer(lecturer);
            logger.info("Lecturer added successfully: ID={}, LecturerID={}", 
                createdLecturer.getId(), createdLecturer.getLecturerId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdLecturer);
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Error adding lecturer (LecturerID={}): {}", 
                lecturer.getLecturerId(), e.getMessage(), e);
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to add lecturer."));
        } catch (Exception e) {
            logger.error("Unexpected error adding lecturer (LecturerID={}): {}", 
                lecturer.getLecturerId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Retrieves all lecturer records.
     * @return ResponseEntity containing list of lecturers or error message
     */
    @GetMapping
    public ResponseEntity<?> getAllLecturers() {
        try {
            List<Lecturer> lecturers = firestoreService.getAllLecturers();
            logger.debug("Retrieved {} lecturers.", lecturers.size());
            return ResponseEntity.ok(lecturers);
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Error retrieving lecturers: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve lecturers."));
        } catch (Exception e) {
            logger.error("Unexpected error retrieving lecturers: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Deletes a lecturer record.
     * @param id The ID of the lecturer to delete
     * @return ResponseEntity with success/error message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLecturer(@PathVariable String id) {
        try {
            firestoreService.deleteLecturer(id);
            logger.info("Lecturer deleted successfully: {}", id);
            return ResponseEntity.noContent().build(); // HTTP 204
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Error deleting lecturer {}: {}", id, e.getMessage(), e);
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to delete lecturer."));
        } catch (Exception e) {
            logger.error("Unexpected error deleting lecturer {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred."));
        }
    }

    // Potential future endpoints:
    // @GetMapping("/{id}") - Get specific lecturer by ID
    // @PutMapping("/{id}") - Update lecturer information
    // @GetMapping("/search") - Search lecturers by name/email
}