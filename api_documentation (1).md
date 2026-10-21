# HostelCare – REST API Reference Manual

---

## Base Configuration
- **Base URL:** `http://localhost:8081`
- **Content-Type:** `application/json`
- **Authentication:** Session-Based / Basic Authentication via Spring Security

---

## Endpoint Overview Table

| Method | Endpoint | Description | Permitted Roles |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/complaints` | Fetch list of all complaints (newest first) | `STUDENT`, `RC`, `STAFF`, `ADMIN` |
| `POST` | `/api/complaints` | Register a new complaint | `STUDENT` |
| `PATCH` | `/api/complaints/{id}/verification` | Verify or reject a complaint | `RC` |
| `PATCH` | `/api/complaints/{id}/assign` | Assign maintenance mechanic to complaint | `RC`, `ADMIN` |
| `PATCH` | `/api/complaints/{id}/status` | Update issue repair status | `STAFF` |
| `GET` | `/api/admin/users` | List all user accounts | `ADMIN` |
| `POST` | `/api/admin/users` | Create a new user account | `ADMIN` |
| `PATCH` | `/api/admin/users/{id}/enabled` | Enable or disable user account | `ADMIN` |

---

## Detailed Endpoint Specifications

### 1. Submit Complaint
- **URL:** `/api/complaints`
- **Method:** `POST`
- **Access:** `STUDENT`

#### Request Body
```json
{
  "studentName": "John Doe",
  "roomNumber": "B-304",
  "category": "Plumbing",
  "priority": "Medium",
  "title": "Leaking Pipe in Washroom",
  "description": "The main pipe under the sink is leaking water continuously."
}
```

#### Responses
- **201 Created**
```json
{
  "id": 1,
  "studentName": "John Doe",
  "roomNumber": "B-304",
  "category": "Plumbing",
  "priority": "Medium",
  "title": "Leaking Pipe in Washroom",
  "description": "The main pipe under the sink is leaking water continuously.",
  "status": "Pending",
  "verificationStatus": "Pending",
  "verifiedBy": null,
  "assignedMechanic": null,
  "createdAt": "2026-10-21T08:30:00"
}
```
- **400 Bad Request**
```json
{
  "error": "Bad Request",
  "message": "Required fields are missing or invalid."
}
```

---

### 2. Verify Complaint
- **URL:** `/api/complaints/{id}/verification`
- **Method:** `PATCH`
- **Access:** `RC`

#### Request Body
```json
{
  "verificationStatus": "Verified",
  "verifiedBy": "RC Ramesh"
}
```

#### Responses
- **200 OK**
```json
{
  "id": 1,
  "verificationStatus": "Verified",
  "verifiedBy": "RC Ramesh",
  "status": "Pending"
}
```
- **400 Bad Request**
```json
{
  "error": "Bad Request",
  "message": "Invalid verification status. Must be 'Verified' or 'Rejected'."
}
```
- **404 Not Found**
```json
{
  "error": "Not Found",
  "message": "Complaint with ID 1 does not exist."
}
```

---

### 3. Assign Mechanic
- **URL:** `/api/complaints/{id}/assign`
- **Method:** `PATCH`
- **Access:** `RC`, `ADMIN`

#### Request Body
```json
{
  "assignedMechanic": "Suresh (Plumber)"
}
```

#### Responses
- **200 OK**
```json
{
  "id": 1,
  "assignedMechanic": "Suresh (Plumber)",
  "verificationStatus": "Verified"
}
```
- **400 Bad Request (Unverified Constraint)**
```json
{
  "error": "Bad Request",
  "message": "Cannot assign mechanic to unverified complaint."
}
```

---

### 4. Update Repair Status
- **URL:** `/api/complaints/{id}/status`
- **Method:** `PATCH`
- **Access:** `STAFF`

#### Request Body
```json
{
  "status": "In Progress"
}
```
*(Valid status values: `Pending`, `In Progress`, `Resolved`)*

#### Responses
- **200 OK**
```json
{
  "id": 1,
  "status": "In Progress"
}
```

---

### 5. Create User Account
- **URL:** `/api/admin/users`
- **Method:** `POST`
- **Access:** `ADMIN`

#### Request Body
```json
{
  "username": "stud@1234",
  "password": "SecurePassword123!",
  "role": "STUDENT"
}
```

#### Responses
- **201 Created**
```json
{
  "id": 10,
  "username": "stud@1234",
  "role": "STUDENT",
  "enabled": true
}
```
*(Note: Passwords are excluded from JSON responses).*

---

### 6. Toggle Account Active State
- **URL:** `/api/admin/users/{id}/enabled`
- **Method:** `PATCH`
- **Access:** `ADMIN`

#### Request Body
```json
{
  "enabled": false
}
```

#### Responses
- **200 OK**
```json
{
  "id": 10,
  "username": "stud@1234",
  "enabled": false
}
```