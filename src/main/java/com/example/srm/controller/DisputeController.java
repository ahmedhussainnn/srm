package com.example.srm.controller;

import com.example.srm.model.Dispute;
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
 * REST controller for managing grade dispute operations.
 * Provides endpoints for creating and retrieving dispute records.
 * All endpoints are prefixed with '/api/disputes'.
 */
@RestController
@RequestMapping("/api/disputes")
public class DisputeController {

    private static final Logger logger = LoggerFactory.getLogger(DisputeController.class);
    private final FirestoreService firestoreService;

    /**
     * Constructor for dependency injection of FirestoreService.
     * @param firestoreService The service layer for Firestore operations
     */
    public DisputeController(FirestoreService firestoreService) {
        this.firestoreService = firestoreService;
    }

    /**
     * Creates a new grade dispute record.
     * @param dispute The dispute object containing student, course, and reason details
     * @return ResponseEntity containing the created dispute or error message
     */
    @PostMapping
    public ResponseEntity<?> addDispute(@RequestBody Dispute dispute) {
        try {
            // Validate required fields
            if (dispute.getRollNumber() == null || dispute.getRollNumber().isEmpty() ||
                dispute.getCourseCode() == null || dispute.getCourseCode().isEmpty() ||
                dispute.getReason() == null || dispute.getReason().isEmpty()) {
                logger.warn("Add dispute request failed validation: {}", dispute);
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Roll number, course code, and reason are required."
                ));
            }

            // Set default status if not provided
            if (dispute.getStatus() == null || dispute.getStatus().isEmpty()) {
                dispute.setStatus("pending");
                logger.debug("Setting default 'pending' status for new dispute");
            }

            Dispute createdDispute = firestoreService.addDispute(dispute);
            logger.info("Dispute added successfully: ID={}, Roll={}, Course={}", 
                createdDispute.getId(), createdDispute.getRollNumber(), createdDispute.getCourseCode());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDispute);
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Error adding dispute (Roll={}, Course={}): {}", 
                dispute.getRollNumber(), dispute.getCourseCode(), e.getMessage(), e);
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to add dispute."));
        } catch (Exception e) {
            logger.error("Unexpected error adding dispute (Roll={}, Course={}): {}", 
                dispute.getRollNumber(), dispute.getCourseCode(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Retrieves all dispute records.
     * @return ResponseEntity containing list of disputes or error message
     */
    @GetMapping
    public ResponseEntity<?> getAllDisputes() {
        try {
            List<Dispute> disputes = firestoreService.getAllDisputes();
            logger.debug("Retrieved {} disputes.", disputes.size());
            return ResponseEntity.ok(disputes);
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Error retrieving disputes: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve disputes."));
        } catch (Exception e) {
            logger.error("Unexpected error retrieving disputes: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred."));
        }
    }

    // Future endpoints that could be implemented:
    // @GetMapping("/{id}") - Get specific dispute by ID
    // @PutMapping("/{id}") - Update dispute status (e.g., from pending to resolved)
    // @DeleteMapping("/{id}") - Remove dispute record
    // @GetMapping("/student/{rollNumber}") - Get disputes by student
    // @GetMapping("/course/{courseCode}") - Get disputes by course
}