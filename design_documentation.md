# HostelCare – System Design & Architecture Report

---

## 1. Problem Statement & Objectives

### Problem Statement
Hostel maintenance management in academic institutions relies heavily on manual paper registers or informal word-of-mouth communication. This introduces operational challenges:
- Difficulty in tracking complaint statuses in real time.
- Lack of accountability and verification before delegating repair jobs.
- Absence of automated escalation for unresolved long-standing issues.
- Security and management gaps regarding user authentication and administrative logs.

### Objectives
- Develop a unified, role-based digital management system.
- Streamline complaint tracking through clear stages: Submission $\rightarrow$ Verification $\rightarrow$ Assignment $\rightarrow$ Resolution.
- Implement background escalation for neglected complaints.
- Secure data access via Spring Security role management.

---

## 2. Requirement Analysis

### Functional Requirements
1. **Authentication:** Users log in with secure credentials; access is restricted based on assigned roles (`ADMIN`, `STUDENT`, `RC`, `STAFF`).
2. **Complaint Management:**
   - Students submit issue details (room, category, priority, description).
   - RCs review and assign status `Verified` or `Rejected`.
   - Mechanics are assigned only to `Verified` complaints.
   - Staff transition repair states (`Pending` $\rightarrow$ `In Progress` $\rightarrow$ `Resolved`).
3. **Account Administration:** Admins create, list, and enable/disable system user accounts.
4. **Automated Priority Escalation:** Automatic system updates for overdue complaints.

### Non-Functional Requirements
- **Security:** Passwords encoded using BCrypt hash functions. Role access enforced at API endpoints.
- **Data Integrity:** Database constraints guarantee complete required fields.
- **Performance:** Asynchronous REST endpoints using JSON payloads.

---

## 3. System Architecture & Layered Design

```
+-----------------------------------------------------------+
|                      Presentation Layer                   |
|       (HTML5, CSS3, JavaScript - Fetch API, Single UI)    |
+-----------------------------------------------------------+
                               |
                               v HTTP REST / JSON
+-----------------------------------------------------------+
|                       Controller Layer                    |
|       (ComplaintController, AdminController, Auth)        |
+-----------------------------------------------------------+
                               |
                               v Internal Service Calls
+-----------------------------------------------------------+
|                  Service / Business Logic                 |
|    (Escalation Logic, Verification Checks, BCrypt)        |
+-----------------------------------------------------------+
                               |
                               v JPA / Hibernate
+-----------------------------------------------------------+
|                       Data Access Layer                   |
|        (ComplaintRepository, UserRepository)              |
+-----------------------------------------------------------+
                               |
                               v JDBC
+-----------------------------------------------------------+
|                        Database Layer                     |
|                         (MySQL DB)                        |
+-----------------------------------------------------------+
```

---

## 4. Entity Relationship (ER) Data Model

### Data Dictionary

#### Entity: `users` (`UserEntity`)
| Attribute | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique user identifier |
| `username` | VARCHAR(50) | UNIQUE, NOT NULL | Account login ID |
| `password` | VARCHAR(255) | NOT NULL | BCrypt hashed password |
| `role` | VARCHAR(20) | NOT NULL | Role (`ADMIN`, `STUDENT`, `RC`, `STAFF`) |
| `enabled` | BOOLEAN | NOT NULL, DEFAULT true | Account active state flag |

#### Entity: `complaints` (`ComplaintEntity`)
| Attribute | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique complaint identifier |
| `student_name` | VARCHAR(100) | NOT NULL | Name of student reporting |
| `room_number` | VARCHAR(20) | NOT NULL | Hostel room number |
| `category` | VARCHAR(50) | NOT NULL | Complaint classification |
| `priority` | VARCHAR(20) | NOT NULL | Priority level (`Low`, `Medium`, `High`) |
| `title` | VARCHAR(150) | NOT NULL | Short issue title |
| `description` | TEXT | NOT NULL | Detailed description |
| `status` | VARCHAR(30) | NOT NULL, DEFAULT 'Pending' | Repair status (`Pending`, `In Progress`, `Resolved`) |
| `verification_status`| VARCHAR(30)| NOT NULL, DEFAULT 'Pending' | RC status (`Pending`, `Verified`, `Rejected`) |
| `verified_by` | VARCHAR(100) | NULLABLE | Name of the verifying RC |
| `assigned_mechanic` | VARCHAR(100) | NULLABLE | Name of assigned technician |
| `created_at` | TIMESTAMP | NOT NULL | System creation timestamp |

---

## 5. Workflows & State Transitions

### Complaint Lifecycle Sequence
```
[STUDENT] ---> Submit Complaint ---> (Status: Pending, Verification: Pending)
                                            |
                                            v
                                     [RC Verification]
                                     /               \
                             (Verified)             (Rejected)
                                 |                       |
                                 v                       v
                         [STAFF Assignment]        [Process Ends]
                                 |
                                 v
                         (Status: In Progress)
                                 |
                                 v
                         (Status: Resolved)
```

---

## 6. Security Design

1. **Authentication:** Form-based authentication configured via Spring Security HTTP Security chains.
2. **Password Encryption:** All passwords are stored hashed with BCrypt salt factors.
3. **Endpoint Authorization Matrix:**

| Endpoint Pattern | Method | Permitted Roles |
| :--- | :--- | :--- |
| `/api/admin/**` | ALL | `ADMIN` |
| `/api/complaints/*/verification` | PATCH | `RC` |
| `/api/complaints/*/assign` | PATCH | `RC`, `ADMIN` |
| `/api/complaints/*/status` | PATCH | `STAFF` |
| `/api/complaints` | POST | `STUDENT` |
| `/api/complaints/**` | GET | `STUDENT`, `RC`, `STAFF`, `ADMIN` |