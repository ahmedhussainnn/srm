package com.example.srm.service;

import com.google.firebase.FirebaseApp;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.example.srm.model.*; // Import all models
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class FirestoreService {

    private static final Logger logger = LoggerFactory.getLogger(FirestoreService.class);
    private static final String STUDENTS_COLLECTION = "Students";
    private static final String COURSES_COLLECTION = "Courses";
    private static final String LECTURERS_COLLECTION = "Lecturers";
    private static final String RESULTS_COLLECTION = "Results"; // Assuming collection name
    private static final String DISPUTES_COLLECTION = "Disputes";

    private Firestore getDb() {
        // Ensure FirebaseApp is initialized before calling this
         if (FirebaseApp.getApps().isEmpty()) {
            logger.error("FirebaseApp not initialized. Firestore client cannot be retrieved.");
            throw new IllegalStateException("FirebaseApp not initialized.");
        }
        return FirestoreClient.getFirestore();
    }

    // --- Student Methods ---

    public Student addStudent(Student student) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getDb().collection(STUDENTS_COLLECTION).document();
        student.setId(docRef.getId()); // Set the auto-generated ID
        ApiFuture<WriteResult> future = docRef.set(student);
        logger.info("Added student {} at {}", student.getId(), future.get().getUpdateTime());
        return student;
    }

    public Student getStudent(String studentId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getDb().collection(STUDENTS_COLLECTION).document(studentId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            Student student = document.toObject(Student.class);
            if (student != null) { // Set the ID after deserialization
                 student.setId(document.getId());
            }
            return student;
        } else {
            return null;
        }
    }

     public List<Student> getAllStudents() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = getDb().collection(STUDENTS_COLLECTION).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Student> students = new ArrayList<>();
        for (DocumentSnapshot document : documents) {
             Student student = document.toObject(Student.class);
             if (student != null) {
                 student.setId(document.getId());
                 students.add(student);
             }
        }
        return students;
    }

     public void updateStudent(String studentId, Map<String, Object> updatedData) throws ExecutionException, InterruptedException {
         // Remove 'id' field from update map if present, as it should not be changed
         updatedData.remove("id");
         DocumentReference docRef = getDb().collection(STUDENTS_COLLECTION).document(studentId);
         ApiFuture<WriteResult> future = docRef.update(updatedData);
         logger.info("Updated student {} at {}", studentId, future.get().getUpdateTime());
     }

     public void deleteStudent(String studentId) throws ExecutionException, InterruptedException {
         ApiFuture<WriteResult> future = getDb().collection(STUDENTS_COLLECTION).document(studentId).delete();
         logger.info("Deleted student {} at {}", studentId, future.get().getUpdateTime());
     }


    // --- Course Methods ---

    public Course addCourse(Course course) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getDb().collection(COURSES_COLLECTION).document();
        course.setId(docRef.getId());
        ApiFuture<WriteResult> future = docRef.set(course);
        logger.info("Added course {} at {}", course.getId(), future.get().getUpdateTime());
        return course;
    }

    public List<Course> getAllCourses() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = getDb().collection(COURSES_COLLECTION).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Course> courses = new ArrayList<>();
        for (DocumentSnapshot document : documents) {
            Course course = document.toObject(Course.class);
             if (course != null) {
                 course.setId(document.getId());
                 courses.add(course);
             }
        }
        return courses;
    }

     public void updateCourse(String courseId, Map<String, Object> updatedData) throws ExecutionException, InterruptedException {
         updatedData.remove("id");
         DocumentReference docRef = getDb().collection(COURSES_COLLECTION).document(courseId);
         ApiFuture<WriteResult> future = docRef.update(updatedData);
         logger.info("Updated course {} at {}", courseId, future.get().getUpdateTime());
     }

     public void deleteCourse(String courseId) throws ExecutionException, InterruptedException {
         ApiFuture<WriteResult> future = getDb().collection(COURSES_COLLECTION).document(courseId).delete();
         logger.info("Deleted course {} at {}", courseId, future.get().getUpdateTime());
     }

    // --- Lecturer Methods ---

     public Lecturer addLecturer(Lecturer lecturer) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getDb().collection(LECTURERS_COLLECTION).document();
        lecturer.setId(docRef.getId());
        ApiFuture<WriteResult> future = docRef.set(lecturer);
        logger.info("Added lecturer {} at {}", lecturer.getId(), future.get().getUpdateTime());
        return lecturer;
    }

    public List<Lecturer> getAllLecturers() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = getDb().collection(LECTURERS_COLLECTION).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Lecturer> lecturers = new ArrayList<>();
        for (DocumentSnapshot document : documents) {
             Lecturer lecturer = document.toObject(Lecturer.class);
             if (lecturer != null) {
                 lecturer.setId(document.getId());
                 lecturers.add(lecturer);
             }
        }
        return lecturers;
    }

     public void deleteLecturer(String lecturerId) throws ExecutionException, InterruptedException {
         ApiFuture<WriteResult> future = getDb().collection(LECTURERS_COLLECTION).document(lecturerId).delete();
         logger.info("Deleted lecturer {} at {}", lecturerId, future.get().getUpdateTime());
     }
     // Add updateLecturer if needed

    // --- Result Methods ---
      public Result addResult(Result result) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getDb().collection(RESULTS_COLLECTION).document();
        result.setId(docRef.getId());
        ApiFuture<WriteResult> future = docRef.set(result);
        logger.info("Added result {} at {}", result.getId(), future.get().getUpdateTime());
        return result;
    }

    public List<Result> getAllResults() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = getDb().collection(RESULTS_COLLECTION).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Result> results = new ArrayList<>();
        for (DocumentSnapshot document : documents) {
            Result result = document.toObject(Result.class);
             if (result != null) {
                 result.setId(document.getId());
                 results.add(result);
             }
        }
        return results;
    }

     public void deleteResult(String resultId) throws ExecutionException, InterruptedException {
         ApiFuture<WriteResult> future = getDb().collection(RESULTS_COLLECTION).document(resultId).delete();
         logger.info("Deleted result {} at {}", resultId, future.get().getUpdateTime());
     }
      // Add updateResult if needed


    // --- Dispute Methods ---
      public Dispute addDispute(Dispute dispute) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getDb().collection(DISPUTES_COLLECTION).document();
        dispute.setId(docRef.getId());
        ApiFuture<WriteResult> future = docRef.set(dispute);
        logger.info("Added dispute {} at {}", dispute.getId(), future.get().getUpdateTime());
        return dispute;
    }

    public List<Dispute> getAllDisputes() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = getDb().collection(DISPUTES_COLLECTION).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Dispute> disputes = new ArrayList<>();
        for (DocumentSnapshot document : documents) {
            Dispute dispute = document.toObject(Dispute.class);
             if (dispute != null) {
                 dispute.setId(document.getId());
                 disputes.add(dispute);
             }
        }
        return disputes;
    }
    // Add updateDispute/deleteDispute if needed
}
