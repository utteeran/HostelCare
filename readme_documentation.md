# HostelCare – Hostel Complaint Tracker

**HostelCare** is a web-based Hostel Complaint Management System developed with Spring Boot and MySQL. It replaces manual paper registers and informal reporting with a structured, role-based digital platform where students report issues, Resident Counselors (RCs) verify complaints, maintenance staff process repairs, and administrators manage user accounts.

---

## Table of Contents
- [Features](#features)
- [Technology Stack](#technology-stack)
- [System Architecture](#system-architecture)
- [User Roles & Permissions](#user-roles--permissions)
- [Prerequisites](#prerequisites)
- [Database Configuration](#database-configuration)
- [Build and Installation](#build-and-installation)
- [Running the Application](#running-the-application)
- [Sample API Requests](#sample-api-requests)
- [Automated Tasks](#automated-tasks)
- [Testing & Verification](#testing--verification)
- [Future Enhancements](#future-enhancements)

---

## Features

- **Role-Based Access Control (RBAC):** Access control enforced by Spring Security for `ADMIN`, `STUDENT`, `RC`, and `STAFF`.
- **Complaint Submission & Tracking:** Students submit hostel issues and monitor resolution status in real time.
- **Verification Workflow:** Resident Counselors verify or reject incoming complaints before maintenance assignment.
- **Maintenance Assignment & Status Tracking:** Maintenance staff are assigned verified tasks and update statuses (`Pending` $\rightarrow$ `In Progress` $\rightarrow$ `Resolved`).
- **Administrative Control:** Admins can view, create, enable, and disable user accounts.
- **Automatic Priority Escalation:** A background `@Scheduled` job escalates `Medium` priority complaints older than 5 days to `High` priority automatically.

---

## Technology Stack

- **Backend Framework:** Java 17+, Spring Boot 3.x
- **Security:** Spring Security (BCrypt password encoding)
- **Persistence & ORM:** Spring Data JPA / Hibernate
- **Database:** MySQL 8.0+
- **Frontend Layer:** HTML5, CSS3, Vanilla JavaScript (Fetch API)
- **Build Tool:** Apache Maven
- **IDE:** IntelliJ IDEA / Eclipse / VS Code

---

## System Architecture

HostelCare uses a standard layered architectural pattern:

1. **Presentation Layer:** Static HTML pages (`login.html`, `admin.html`, `verify.html`, `staff.html`, `track.html`) interacting via asynchronous Fetch API requests.
2. **Controller Layer:** REST Controllers managing HTTP requests, routing, payload validation, and HTTP responses.
3. **Service Layer:** Encapsulates business rules (verification enforcement, account lifecycle, auto-escalation).
4. **Data Access Layer:** Spring Data JPA Repositories executing database transactions against MySQL.

---

## User Roles & Permissions

| Role | Responsibilities & Access |
| :--- | :--- |
| **`STUDENT`** | Submits complaints, views tracking status via complaint ID. |
| **`RC`** | Reviews incoming complaints; sets verification status to `Verified` or `Rejected`. |
| **`STAFF`** | Views verified complaints, takes assignment, updates repair status (`In Progress`, `Resolved`). |
| **`ADMIN`** | Manages user system accounts (Create, List, Enable/Disable). |

---

## Prerequisites

Ensure you have the following installed locally:
- **JDK 17** or higher
- **Maven 3.8+**
- **MySQL Server 8.0+**
- **Git**

---

## Database Configuration

1. Log in to MySQL and create the database:
   ```sql
   CREATE DATABASE hostelcare;
   ```
2. Open `src/main/resources/application.properties` and update the connection parameters:
   ```properties
   server.port=8081

   # MySQL Database Configuration
   spring.datasource.url=jdbc:mysql://localhost:3306/hostelcare?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   spring.datasource.username=root
   spring.datasource.password=your_mysql_password
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

   # Hibernate / JPA Settings
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
   ```

---

## Build and Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/hostelcare.git
   cd hostelcare
   ```
2. Build the project using Maven:
   ```bash
   mvn clean package -DskipTests
   ```

---

## Running the Application

### Option 1: Via Maven
```bash
mvn spring-boot:run
```

### Option 2: Via Executable JAR
```bash
java -jar target/hostelcare-0.0.1-SNAPSHOT.jar
```

Once running, access the application in your browser at:
`http://localhost:8081`

---

## Sample API Requests

### 1. Submit a Complaint (`POST /api/complaints`)
**cURL:**
```bash
curl -X POST http://localhost:8081/api/complaints \
  -H "Content-Type: application/json" \
  -d '{
    "studentName": "John Doe",
    "roomNumber": "B-304",
    "category": "Plumbing",
    "priority": "Medium",
    "title": "Leaking Pipe in Washroom",
    "description": "The main pipe under the sink is leaking water continuously."
  }'
```

### 2. Verify a Complaint (`PATCH /api/complaints/1/verification`)
**cURL:**
```bash
curl -X PATCH http://localhost:8081/api/complaints/1/verification \
  -H "Content-Type: application/json" \
  -d '{
    "verificationStatus": "Verified",
    "verifiedBy": "RC Ramesh"
  }'
```

### 3. Assign Maintenance Mechanic (`PATCH /api/complaints/1/assign`)
**cURL:**
```bash
curl -X PATCH http://localhost:8081/api/complaints/1/assign \
  -H "Content-Type: application/json" \
  -d '{
    "assignedMechanic": "Suresh (Plumber)"
  }'
```

---

## Automated Tasks

HostelCare executes a scheduled maintenance task enabled via `@EnableScheduling`:
- **Frequency:** Every 1 hour (`@Scheduled(fixedRate = 3600000)`).
- **Rule:** Scans all complaints with status `Pending` or `In Progress` and priority `Medium`.
- **Action:** If the elapsed time since creation exceed 5 days, priority is updated to `High`.

---

## Testing & Verification Checklist

- [x] Unauthenticated users are redirected to login.
- [x] Students can submit complaints and track via Complaint ID.
- [x] Resident Counselors can verify or reject complaints.
- [x] Mechanics cannot be assigned to unverified complaints.
- [x] Maintenance staff can update statuses to `In Progress` or `Resolved`.
- [x] Admin can disable user accounts, preventing subsequent logins.
- [x] Passwords are encrypted in MySQL via BCrypt and excluded from REST response payloads.

---

## Future Enhancements

- OAuth2 / JWT stateless session authentication.
- File and photo attachment uploads for complaint evidence.
- Real-time WebSockets/SMS/Email notifications upon status updates.
- Pagination, sorting, and advanced keyword search for administrative analytics.