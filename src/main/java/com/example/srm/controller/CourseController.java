package com.example.srm.controller;

import com.example.srm.model.Course;
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
 * REST controller for managing course-related operations.
 * Provides CRUD endpoints for course records in Firestore.
 * All endpoints are prefixed with '/api/courses'.
 */
@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);
    private final FirestoreService firestoreService;

    /**
     * Constructor for dependency injection of FirestoreService.
     * @param firestoreService The service layer for Firestore operations
     */
    public CourseController(FirestoreService firestoreService) {
        this.firestoreService = firestoreService;
    }

    /**
     * Creates a new course record.
     * @param course The course object to be created (from request body)
     * @return ResponseEntity containing the created course or error message
     */
    @PostMapping
    public ResponseEntity<?> addCourse(@RequestBody Course course) {
        try {
            // Validate required fields
            if (course.getCourseCode() == null || course.getCourseCode().isEmpty() ||
                course.getCourseName() == null || course.getCourseName().isEmpty() ||
                course.getCourseInstructor() == null || course.getCourseInstructor().isEmpty()) {
                logger.warn("Add course request failed validation: {}", course);
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Course code, name, and instructor are required."
                ));
            }

            Course createdCourse = firestoreService.addCourse(course);
            logger.info("Course added successfully: ID={}, Code={}", 
                createdCourse.getId(), createdCourse.getCourseCode());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCourse);
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Error adding course (Code={}): {}", 
                course.getCourseCode(), e.getMessage(), e);
            Thread.currentThread().interrupt(); // Restore interrupt status
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to add course. Please try again later."));
        } catch (Exception e) {
            logger.error("Unexpected error adding course (Code={}): {}", 
                course.getCourseCode(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Retrieves all course records.
     * @return ResponseEntity containing list of courses or error message
     */
    @GetMapping
    public ResponseEntity<?> getAllCourses() {
        try {
            List<Course> courses = firestoreService.getAllCourses();
            logger.debug("Retrieved {} courses.", courses.size());
            return ResponseEntity.ok(courses);
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Error retrieving courses: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve courses. Please try again later."));
        } catch (Exception e) {
            logger.error("Unexpected error retrieving courses: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Updates a course record.
     * @param id The ID of the course to update
     * @param updatedData Map containing fields to update
     * @return ResponseEntity with success/error message
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable String id, 
                                        @RequestBody Map<String, Object> updatedData) {
        // Validate update payload
        if (updatedData == null || updatedData.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Update data cannot be empty."));
        }

        try {
            firestoreService.updateCourse(id, updatedData);
            logger.info("Course updated successfully: {}", id);
            return ResponseEntity.ok(Map.of(
                "message", "Course updated successfully", 
                "id", id
            ));
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Error updating course {}: {}", id, e.getMessage(), e);
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to update course. Please try again later."));
        } catch (Exception e) {
            logger.error("Unexpected error updating course {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred during update."));
        }
    }

    /**
     * Deletes a course record.
     * @param id The ID of the course to delete
     * @return ResponseEntity with success/error message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable String id) {
        try {
            firestoreService.deleteCourse(id);
            logger.info("Course deleted successfully: {}", id);
            return ResponseEntity.noContent().build(); // HTTP 204
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Error deleting course {}: {}", id, e.getMessage(), e);
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to delete course. Please try again later."));
        } catch (Exception e) {
            logger.error("Unexpected error deleting course {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred during deletion."));
        }
    }
}