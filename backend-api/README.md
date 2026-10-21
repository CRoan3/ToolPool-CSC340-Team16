# ToolPool Backend API

Spring Boot + PostgreSQL (Neon) REST API for ToolPool, a peer-to-peer tool rental platform.

**Team:** Christopher Roan ([@CRoan3](https://github.com/CRoan3)), Nobert Akas ([@mtunelife](https://github.com/mtunelife))

# UML Class Diagram

![Class Diagram](../docs/uml-class.png)

Editable source: [`docs/uml-class.puml`](../docs/uml-class.puml)

# API Overview

**Base URL:**

- local: http://localhost:8080/api

JSON request bodies are validated. Errors return a JSON body with a message:

```json
{ "error": "An account with that email already exists." }
```

| Code              | Meaning                                                              |
| ----------------- | -------------------------------------------------------------------- |
| `400 Bad Request` | Invalid input (missing/blank required field, bad email, etc.)        |
| `404 Not Found`   | The id in the path does not exist                                    |
| `409 Conflict`    | Duplicate record (e.g. email already in use) or invalid state change |

Objects are linked by id in request bodies (for example `"ownerId": 1`), not by nested objects.

## Users

| Method | Path              | Purpose                                              | Response code | Error codes     | User story                                             | Implemented by   |
| ------ | ----------------- | ---------------------------------------------------- | ------------- | --------------- | ------------------------------------------------------ | ---------------- |
| `POST` | `/api/users`      | Create an account                                    | `201 Created` | `400, 409`      | [US-1](../docs/Requirements%20-%20ToolPool.md#L55)     | Christopher Roan |
| `GET`  | `/api/users/{id}` | View a user's profile                                | `200 OK`      | `404`           | [Story 7](../docs/Requirements%20-%20ToolPool.md#L111) | Christopher Roan |
| `PUT`  | `/api/users/{id}` | Edit a user's profile (name, email, phone, location) | `200 OK`      | `400, 404, 409` | [Story 7](../docs/Requirements%20-%20ToolPool.md#L111) | Christopher Roan |
| `GET`    | `/api/users`             | Admin: list all users (optional `?accountStatus=ACTIVE` or `BANNED`) | `200 OK`         | `400`           | [Story 1](../docs/Requirements%20-%20ToolPool.md#L99) | Christopher Roan |
| `PUT`    | `/api/users/{id}/status` | Admin: ban or unban a user                                           | `200 OK`         | `400, 404`      | [Story 1](../docs/Requirements%20-%20ToolPool.md#L99) | Christopher Roan |
| `DELETE` | `/api/users/{id}`        | Admin: permanently remove a user                                     | `204 No Content` | `404, 409`      | [Story 1](../docs/Requirements%20-%20ToolPool.md#L99) | Christopher Roan |

New users are always created with `role` = `USER` and `accountStatus` = `ACTIVE`; these fields
cannot be set in the request. Passwords are accepted on registration and never included in responses.

`accountStatus` can only be changed through the admin endpoint `PUT /api/users/{id}/status`.
A user can only be deleted if they have no tools, rentals, reviews, or reports; otherwise the API
returns `409 Conflict` and the admin should ban the user instead.

### Example: Create a user

```http
POST /api/users
Content-Type: application/json

{
  "name": "Chris Roan",
  "email": "chris@example.com",
  "password": "secret123",
  "phoneNumber": "336-555-0101",
  "location": "Greensboro, NC"
}
```

| Field         | Required | Rules                                                      |
| ------------- | -------- | ---------------------------------------------------------- |
| `name`        | yes      | not blank, max 100 characters                              |
| `email`       | yes      | valid email, max 254 characters, unique (case-insensitive) |
| `password`    | yes      | 8–72 characters                                            |
| `phoneNumber` | no       | max 20 characters                                          |
| `location`    | no       | max 100 characters                                         |

Response `201 Created` (with header `Location: /api/users/1`):

```json
{
  "id": 1,
  "name": "Chris Roan",
  "email": "chris@example.com",
  "phoneNumber": "336-555-0101",
  "location": "Greensboro, NC",
  "role": "USER",
  "accountStatus": "ACTIVE",
  "createdAt": "2026-10-18T15:18:02.815116"
}
```

### Example: Edit a user's profile

```http
PUT /api/users/1
Content-Type: application/json

{
  "name": "Christopher Roan",
  "email": "chris@example.com",
  "phoneNumber": "336-555-0199",
  "location": "Winston-Salem, NC"
}
```

`name` and `email` are required, with the same rules as registration. Response `200 OK` with the updated user.

### Example: Ban a user (admin)

```http
PUT /api/users/2/status
Content-Type: application/json

{
  "accountStatus": "BANNED"
}
```

| Field           | Required | Rules                                     |
| --------------- | -------- | ----------------------------------------- |
| `accountStatus` | yes      | `ACTIVE` or `BANNED` (any capitalization) |

Response `200 OK` with the updated user (`"accountStatus": "BANNED"`).

## Reports

| Method | Path                      | Purpose                                                                   | Response code | Error codes | User story                                             | Implemented by   |
| ------ | ------------------------- | ------------------------------------------------------------------------- | ------------- | ----------- | ------------------------------------------------------ | ---------------- |
| `POST` | `/api/users/{id}/reports` | Report user `{id}` for suspicious behavior                                | `201 Created` | `400, 404`  | [Story 6](../docs/Requirements%20-%20ToolPool.md#L109) | Christopher Roan |
| `GET`  | `/api/reports`            | Admin: list reports, newest first (optional `?status=OPEN` or `RESOLVED`) | `200 OK`      | `400`       | [Story 6](../docs/Requirements%20-%20ToolPool.md#L109) | Christopher Roan |
| `PUT`  | `/api/reports/{id}`       | Admin: mark a report `OPEN` or `RESOLVED`                                 | `200 OK`      | `400, 404`  | [Story 6](../docs/Requirements%20-%20ToolPool.md#L109) | Christopher Roan |

A report links two users: the **reporter** (who filed it) and the **reported user** (`{id}` in the path).
New reports always start with `status` = `OPEN`, and `reportDate` is set by the server. A user cannot
report themselves (`400`).

### Example: Report a user

```http
POST /api/users/2/reports
Content-Type: application/json

{
  "reporterId": 1,
  "reason": "Listed tools they do not own and asked for payment outside the app."
}
```

| Field        | Required | Rules                                         |
| ------------ | -------- | --------------------------------------------- |
| `reporterId` | yes      | id of an existing user, different from `{id}` |
| `reason`     | yes      | not blank, max 500 characters                 |

Response `201 Created` (with header `Location: /api/reports/1`):

```json
{
  "id": 1,
  "reason": "Listed tools they do not own and asked for payment outside the app.",
  "status": "OPEN",
  "reportDate": "2026-10-21T12:34:41.639436",
  "reporterId": 1,
  "reporterName": "Chris Roan",
  "reportedUserId": 2,
  "reportedUserName": "Noa"
}
```

### Example: Resolve a report (admin)

```http
PUT /api/reports/1
Content-Type: application/json

{
  "status": "RESOLVED"
}
```

| Field    | Required | Rules                                     |
| -------- | -------- | ----------------------------------------- |
| `status` | yes      | `OPEN` or `RESOLVED` (any capitalization) |

Response `200 OK` with the updated report.

# Running Locally

Requires JDK 25. Set your **Neon dev branch** connection string in the same terminal you run the app from
(never commit it):

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://..."
.\mvnw.cmd spring-boot:run
```
