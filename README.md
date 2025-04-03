# Student Result Management System (Spring Boot Backend)

This Student Result Management System is a web application designed to streamline the process of managing student information, recording results, and generating reports. The backend is implemented using Java and Spring Boot, connecting to a Firebase Firestore database.

![SRM](static/screenshot.png) ## Table of Contents
1. [Introduction](#introduction)
2. [Features](#features)
3. [Technologies](#technologies)
4. [Setup & Running](#setup--running)
5. [Frontend Integration](#frontend-integration)
6. [API Endpoints](#api-endpoints)
7. [Security Note](#security-note)
8. [TODOs / Improvements](#todos--improvements)
9. [Original Credits](#original-credits)


## Introduction

This system simplifies the management of student data within an educational institution. It provides a web interface for administrators/lecturers to manage students, courses, and results, and for students to view their results and raise disputes. This version uses a Spring Boot backend API.

## Video Walkthrough
[https://streamable.com/mbgbpv](https://streamable.com/mbgbpv)

## Features

(Based on implemented API endpoints and intended frontend functionality)
- **User Authentication:** Login/Signup via Firebase. Hardcoded roles for Student/Lecturer access control (basic).
- **Student Management:** Add, View, Delete students (Lecturer/Admin).
- **Course Management:** Add, View, Update, Delete courses (Lecturer/Admin).
- **Result Management:** Add, View, Delete results (Lecturer). Students can view their own results (requires frontend filtering).
- **Dispute Management:** Students can submit disputes. Lecturers can view disputes (requires filtering/role logic).
- **API Driven:** Backend provides RESTful endpoints for frontend interaction.

## Technologies

**Backend:**
- Java 17+
- Spring Boot 3.x (Web, Lombok)
- Firebase Admin SDK (Firestore, Auth)
- Maven

**Frontend (Provided):**
- HTML5
- Bootstrap 4.5.2
- jQuery 3.5.1
- DataTables 1.10.22
- JavaScript (for API calls and dynamic UI)

**Database:**
- Google Firebase Firestore (NoSQL)

## Setup & Running

1.  **Prerequisites:**
    * Java Development Kit (JDK) 17 or newer installed.
    * Apache Maven installed.
    * Access to the internet (for Maven dependencies and Firebase).

2.  **Get the Code:** Clone this repository or use the script that generated this project.

3.  **Firebase Credentials:**
    * This project requires a Firebase Admin SDK service account key file.
    * It is named `student-result-managemen-....json` in `src/main/resources/`.
    * **SECURITY:** This file contains sensitive private keys. 

4.  **Build (Optional):**
    ```bash
    mvn clean package
    ```
    This creates an executable JAR file in the `target/` directory.

5.  **Run the Application:**
    * Using Maven:
        ```bash
        mvn spring-boot:run
        ```
    * Using the JAR file (after building):
        ```bash
        java -jar target/srm-0.0.1-SNAPSHOT.jar
        ```

6.  **Access:** The application will start, typically on `http://localhost:8080`. Open this URL in your web browser.

7.  **Copy Screenshot:** Manually copy the `screenshot.png` file into the `src/main/resources/static/` directory if you want the image to display in this README.

## Frontend Integration

The frontend (`index.html` and associated static assets) is located in `src/main/resources/static/`.

**IMPORTANT:** The JavaScript in `index.html` has been updated from the original to make **live API calls** to the Spring Boot backend.
    - It handles login/signup via `/api/auth/...`.
    - It dynamically loads data for courses, students, results, etc., into DataTables based on user role.
    - It allows adding students, courses, results, and disputes via forms that POST to the API.
    - It includes basic delete functionality with confirmation.
    - **Further Refinements Needed:** The frontend JavaScript is functional but could be improved (error handling, state management, code structure, backend API endpoint efficiency). See [TODOs](#todos--improvements).

## API Endpoints

The backend provides REST API endpoints under the `/api` prefix. Authentication may be required depending on the endpoint and security configuration (not fully implemented yet).

* **Auth:** `POST /api/auth/login`, `POST /api/auth/signup`
* **Courses:** `GET, POST /api/courses`, `PUT, DELETE /api/courses/{id}`
* **Students:** `GET, POST /api/students`, `GET, PUT, DELETE /api/students/{id}`
* **Lecturers:** `GET, POST /api/lecturers`, `DELETE /api/lecturers/{id}` (May add GET/PUT if needed)
* **Results:** `GET, POST /api/results`, `DELETE /api/results/{id}` (May add GET/PUT if needed)
* **Disputes:** `GET, POST /api/disputes` (May add GET/PUT/DELETE by ID if needed)

Refer to the controller classes in `src/main/java/com/example/srm/controller/` for request/response details.

## TODOs / Improvements

* **Robust Auth/Authz:** Implement Spring Security, JWT validation, proper role/permission checks on API endpoints.

* **Input Validation:** Add more robust server-side validation for all API inputs to prevent invalid data and potential security issues.
* **HTTPS:** Ensure the application is deployed and accessed over HTTPS in production.

* **Frontend Refinement:** Improve JS structure (separate files), use a modern framework (React, Vue, Angular) if complexity grows, enhance UI/UX, add better loading/error states.
* **Efficient Data Loading:** Modify backend APIs and frontend calls to fetch only necessary data (e.g., results for *current* student, disputes for *lecturer's* courses) instead of fetching all and filtering client-side. Use pagination for large datasets.
* **Course/Instructor Linking:** Properly link Instructors in the `Course` model/data.
* **Grade Calculation:** Auto-calculate Grade based on Marks during result entry.
* **Comprehensive Validation:** Add detailed server-side validation (email format, marks range, ID existence checks before updates/deletes).
* **Error Handling:** Provide more specific and user-friendly error messages from the API. Implement global exception handling in Spring Boot.
* **Testing:** Add unit and integration tests for services and controllers.
* **Database Indexes:** Configure Firestore indexes for common query patterns.
* **Timestamping:** Add created/updated timestamps to relevant models (Disputes, Results).
* **Dispute Workflow:** Implement status updates (Resolve/Reject) for disputes in the backend and frontend.
* **Admin Role:** Define and implement specific functionalities for an 'admin' role.

## Original Credits

- This system utilizes Bootstrap for styling and layout.
- DataTables plugin is used for displaying data in a tabular format with enhanced features.
