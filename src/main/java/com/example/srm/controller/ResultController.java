package com.example.srm.controller;

import com.example.srm.model.Result;
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
 * REST controller for managing student result operations.
 * Provides endpoints for creating, retrieving, and deleting academic results.
 * All endpoints are prefixed with '/api/results'.
 */
@RestController
@RequestMapping("/api/results")
public class ResultController {

    private static final Logger logger = LoggerFactory.getLogger(ResultController.class);
    private final FirestoreService firestoreService;

    /**
     * Constructor for dependency injection of FirestoreService.
     * @param firestoreService The service layer for Firestore operations
     */
    public ResultController(FirestoreService firestoreService) {
        this.firestoreService = firestoreService;
    }

    /**
     * Creates a new academic result record.
     * @param result The result object containing student, course, marks, and grade
     * @return ResponseEntity containing the created result or error message
     */
    @PostMapping
    public ResponseEntity<?> addResult(@RequestBody Result result) {
        try {
            // Validate required fields and mark range
            if (result.getRollNumber() == null || result.getRollNumber().isEmpty() ||
                result.getCourseCode() == null || result.getCourseCode().isEmpty() ||
                result.getGrade() == null || result.getGrade().isEmpty() ||
                result.getMarks() < 0) {
                logger.warn("Add result request failed validation: {}", result);
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Roll number, course code, grade, and non-negative marks are required."
                ));
            }

            // Consider adding additional validation:
            // - Check if marks are within valid range (e.g., 0-100)
            // - Verify grade consistency with marks
            // - Validate student/course existence

            Result createdResult = firestoreService.addResult(result);
            logger.info("Result added successfully: ID={}, Roll={}, Course={}", 
                createdResult.getId(), createdResult.getRollNumber(), createdResult.getCourseCode());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdResult);
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Error adding result (Roll={}, Course={}): {}", 
                result.getRollNumber(), result.getCourseCode(), e.getMessage(), e);
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to add result."));
        } catch (Exception e) {
            logger.error("Unexpected error adding result (Roll={}, Course={}): {}", 
                result.getRollNumber(), result.getCourseCode(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Retrieves all result records.
     * @return ResponseEntity containing list of results or error message
     */
    @GetMapping
    public ResponseEntity<?> getAllResults() {
        try {
            List<Result> results = firestoreService.getAllResults();
            logger.debug("Retrieved {} results.", results.size());
            return ResponseEntity.ok(results);
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Error retrieving results: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve results."));
        } catch (Exception e) {
            logger.error("Unexpected error retrieving results: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Deletes a specific result record.
     * @param id The Firestore document ID of the result to delete
     * @return ResponseEntity with success/error message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteResult(@PathVariable String id) {
        try {
            firestoreService.deleteResult(id);
            logger.info("Result deleted successfully: {}", id);
            return ResponseEntity.noContent().build(); // HTTP 204 No Content
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Error deleting result {}: {}", id, e.getMessage(), e);
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to delete result."));
        } catch (Exception e) {
            logger.error("Unexpected error deleting result {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred."));
        }
    }

    // Potential future endpoints:
    // @GetMapping("/{id}") - Get specific result by ID
    // @PutMapping("/{id}") - Update result details
    // @GetMapping("/student/{rollNumber}") - Get results by student
    // @GetMapping("/course/{courseCode}") - Get results by course
    // @GetMapping("/search") - Search results with filters
}