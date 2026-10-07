# Echo - Spaced Repetition Memory Assistant

Echo is a powerful backend service built with Spring Boot, designed to help users retain knowledge through an optimized Spaced Repetition System (SRS). It manages notes, topics, and memory items with a built-in scheduling algorithm to ensure you review what matters, exactly when you need to.

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com/blessed-winner/Echo)
[![Java Version](https://img.shields.io/badge/java-21-orange)](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
[![Framework](https://img.shields.io/badge/framework-Spring%20Boot%203.4-brightgreen)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/license-MIT-blue)](LICENSE)

---

## Live API Documentation

Explore and test the Echo API through our hosted Swagger UI:
[https://echo-5je8.onrender.com/swagger-ui/index.html](https://echo-5je8.onrender.com/swagger-ui/index.html)

---

## Features

- **Spaced Repetition Engine**: Uses a algorithm (Ease Factor, Intervals, and Review Counts) to calculate the next optimal review date.
- **Interval Preview**: Predict the interval produced by Again, Hard, Good, and Easy before committing to an answer.
- **Structured Organization**: Organize your learning into Topics, Notes, and Memory Items.
- **Tagging System**: Flexible tagging for cross-referencing and filtering content.
- **Dynamic Search**: Case-insensitive query endpoints (`/topics/search` and `/notes/search`) for real-time frontend search filtering.
- **Automated Data Seeding**: Idempotent data seeder (`DataSeeder` & `SeedService`) that populates initial topics, notes, and memory items for local development and testing.
- **Rescheduling**: Push a card forward by 1 hour, 3 hours, 1 day, 3 days or 1 week, and set a custom reminder time.
- **Secure Authentication**: Robust JWT-based security with Access and Refresh token support, supporting both `HttpOnly` cookies and JSON payload fallbacks.
- **Social Login**: Sign in with Google or GitHub through OAuth2; the callback issues the same JWT pair as a password login.
- **Email Verification & Password Recovery**: Time-limited (15 minutes), single-use tokens stored hashed and delivered by SMTP. Unverified accounts cannot sign in.
- **Rate Limiting**: Bucket4j throttling on sign-in (5 attempts / 2 minutes) and password reset (3 attempts / 10 minutes) per IP and account.
- **Role-Based Access Control**: USER and ADMIN roles, with a dedicated admin surface.
- **Audit Trail**: Authentication events (success, failure, blocked, rate-limit hit) are recorded with IP address, outcome, and failure reason.
- **Real-Time Notifications**: In-app notifications pushed over a STOMP WebSocket, with unread counts, mark-as-read, single delete, and clear-all.
- **Automated Reminders**: Per-item reminders, an hourly due-items check, a daily 08:00 summary, and a delivery queue that flushes every 60 seconds.
- **Progress Tracking**: Real-time statistics including daily review counts, overdue items, upcoming items, and learning streaks.
- **Analytics**: Retention rate, weekly activity, mastered items (5 or more successful reviews), and system-wide admin totals.
- **Admin Console**: List, edit, enable, disable, promote, reset, and force-verify user accounts.
- **API Documentation**: Fully documented with Swagger UI.

---

## Tech Stack

- **Backend**: Java 21, Spring Boot 3.4
- **Security**: Spring Security, JWT (JJWT), OAuth2 (Google, GitHub)
- **Database**: PostgreSQL 17
- **Migrations**: Flyway
- **Mapping**: MapStruct
- **Email**: Spring Mail (SMTP)
- **Real-Time**: Spring WebSocket (STOMP with SockJS fallback)
- **Rate Limiting**: Bucket4j
- **Utilities**: Lombok, springboot3-dotenv
- **Documentation**: Springdoc OpenAPI (Swagger)
- **Testing**: JUnit 5, Mockito, Spring Test, H2 (in-memory)
- **Client**: React 19 + TypeScript + Vite (companion SPA client)
- **Deployment**: Docker, Render-ready, GitHub Actions CI

---

## Getting Started

### Prerequisites

- JDK 21
- Maven 3.x
- PostgreSQL

### Local Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/blessed-winner/Echo.git
   cd Echo/backend
   ```

2. **Configure Environment Variables**:
   Create a `.env` file in the `backend` directory or set the following variables:
   ```env
   DB_URL=jdbc:postgresql://localhost:5432/echo
   DB_USERNAME=your_username
   DB_PASSWORD=your_password
   JWT_SECRET=your_super_secret_high_entropy_key_here
   ```

   Optional variables, required only for the features that depend on them:
   ```env
   PORT=8080                       # API port (defaults to 8080)
   FRONTEND_URL=http://localhost:5173   # CORS origin for the web client
   COOKIE_SECURE=false             # secure flag on the refresh cookie

   # Email delivery: verification links and password resets
   MAIL_HOST=smtp.gmail.com
   MAIL_PORT=587
   MAIL_USERNAME=your_smtp_user
   MAIL_PASSWORD=your_smtp_password

   # OAuth2 social login
   GOOGLE_CLIENT_ID=...
   GOOGLE_CLIENT_SECRET=...
   GITHUB_CLIENT_ID=...
   GITHUB_CLIENT_SECRET=...
   ```

3. **Build and Run**:
   ```bash
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```
   Flyway applies the migrations in `src/main/resources/db/migration` on startup. The idempotent `DataSeeder` initializes realistic topics, notes, and memory items for local development.

4. **Access Swagger UI (Local)**:
   Open http://localhost:8080/swagger-ui/index.html to explore the API locally.

### Run the Tests

```bash
./mvnw clean test
```

Unit tests run with JUnit 5 and Mockito. The integration tests activate the `test` profile (`application-test.yaml`), which swaps PostgreSQL for an in-memory H2 database with `create-drop` schema management, so no external database is needed. `JWT_SECRET` must be set.

---

## Docker Deployment

Build the image:
```bash
docker build -t echo-backend .
```

Run the container:
```bash
docker run -p 8080:8080 \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/echo \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=password \
  -e JWT_SECRET=secret \
  echo-backend
```

---

## API Endpoints (Quick Reference)

### Authentication

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/auth/register` | Create a new account and send the verification email |
| `GET` | `/auth/verify` | Verify an email address with a one-time token |
| `POST` | `/auth/login` | Obtain JWT tokens (rate-limited) |
| `POST` | `/auth/refresh` | Rotate the access and refresh tokens |
| `POST` | `/auth/logout` | Invalidate the refresh cookie |
| `GET` | `/auth/me` | Current user profile |
| `POST` | `/auth/forgot-password` | Request a password reset email |
| `POST` | `/auth/reset` | Set a new password with a one-time token |
| `GET` | `/auth/success` | OAuth2 hand-off of the issued access token |
| `GET` | `/oauth2/authorization/{provider}` | Start a Google or GitHub login |

### Topics

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/topics` | Create a topic |
| `GET` | `/topics` | List all learning topics (paginated) |
| `GET` | `/topics/search` | Search topics by query (`?q=...`) |
| `GET` | `/topics/{id}` | Get a single topic |
| `PUT` | `/topics/{id}` | Update a topic |
| `DELETE` | `/topics/{id}` | Delete a topic |
| `GET` | `/topics/{id}/notes` | Notes belonging to a topic |
| `GET` | `/topics/{id}/due` | Due items for a topic |
| `GET` | `/topics/{id}/memories` | Memory items for a topic |
| `GET` | `/topics/{id}/summary` | Topic statistics |

### Notes

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/notes` | Create a note |
| `GET` | `/notes` | List notes (paginated) |
| `GET` | `/notes/search` | Search notes by query (`?q=...`) |
| `GET` | `/notes/{id}` | Get a single note |
| `PUT` | `/notes/{id}` | Update a note |
| `DELETE` | `/notes/{id}` | Delete a note |
| `GET` | `/notes/{id}/summary` | Note statistics |
| `GET` | `/notes/{noteId}/due` | Due items for a note |
| `POST` | `/notes/{noteId}/tags` | Attach tags to a note |
| `DELETE` | `/notes/{noteId}/tags` | Detach tags from a note |

### Memory Items & Reviews

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/memories` | Create a memory item |
| `GET` | `/memories` | List memory items (paginated) |
| `GET` | `/memories/due` | Get items pending review (optionally filtered by tag) |
| `GET` | `/memories/stats` | Daily reviews, overdue, upcoming and streak |
| `GET` | `/memories/{id}` | Get a single memory item |
| `PUT` | `/memories/{id}` | Update a memory item |
| `DELETE` | `/memories/{id}` | Delete a memory item |
| `POST` | `/memories/{id}/review` | Submit a review (Again, Hard, Good, Easy) |
| `POST` | `/memories/{id}/reschedule` | Push the next review date forward |
| `GET` | `/memories/{id}/reviews` | Review history for an item |
| `GET` | `/memories/{id}/preview-intervals` | Preview the next interval for each rating |
| `GET` | `/reviews/summary` | Review totals for today, this week and overall |
| `GET` | `/reviews/recent` | Most recent reviews |

### Tags

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/tags` | Create a tag |
| `GET` | `/tags` | List all tags |
| `PUT` | `/tags/{id}` | Rename a tag |
| `DELETE` | `/tags/{id}` | Delete a tag |
| `GET` | `/tags/{id}/summary` | Tag statistics |
| `GET` | `/tags/{id}/notes` | Notes using a tag |

### Notifications & Analytics

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/notifications` | Create a notification |
| `GET` | `/notifications` | List my notifications (paginated) |
| `GET` | `/notifications/unread-count` | Number of unread notifications |
| `POST` | `/notifications/{id}/read` | Mark a notification as read |
| `DELETE` | `/notifications/{id}` | Delete a notification |
| `DELETE` | `/notifications` | Clear all notifications |
| `GET` | `/analytics/me` | Personal learning analytics |
| `GET` | `/admin/system/analytics` | System-wide totals (admin only) |

### Administration (ADMIN role required)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/admin/users` | List all users |
| `GET` | `/admin/users/{id}` | Get a user |
| `PUT` | `/admin/users/{id}` | Update a user |
| `DELETE` | `/admin/users/{id}` | Delete a user |
| `PUT` | `/admin/users/{id}/enable` | Enable an account |
| `PUT` | `/admin/users/{id}/disable` | Disable an account |
| `PUT` | `/admin/users/{id}/role` | Change a user's role |
| `PUT` | `/admin/users/{id}/reset-password` | Reset a user's password |
| `PUT` | `/admin/users/{id}/force-verify` | Verify an account manually |

---

## Security

The application uses JWT Authentication.

1. Obtain a token via `/auth/login`.
2. Include the token in the header of subsequent requests:
   `Authorization: Bearer <your_access_token>`

Additional safeguards:

- **Token lifetimes**: access tokens expire after 10 minutes, refresh tokens after 7 days. Every refresh rotates both tokens.
- **Refresh delivery**: the refresh token is returned in the response body *and* set as an `HttpOnly` cookie scoped to `/auth/refresh`. The cookie cannot be read from JavaScript, and `POST /auth/refresh` accepts either source.
- **OAuth2**: Google and GitHub logins are converted into local users and redirected to `FRONTEND_URL/auth/success` with the access token.
- **Account verification**: tokens for email verification and password reset are stored as SHA-256 hashes, expire after 15 minutes and are deleted after a single use.
- **Authorization**: `/admin/**` requires the `ADMIN` role, and every read or write on a topic, note, memory item, tag or notification is checked against the owning user (403 otherwise).
- **CORS**: only the origin configured in `app.frontend-url` is allowed, with credentials enabled.
- **Audit logging**: login attempts, verification results, password resets and rate-limit hits are written to the `audit_log` table.

---

## Notifications & Reminders

Echo generates and delivers reminders entirely on the server side:

- **Per-item reminders**: whenever an item is created, updated, reviewed or rescheduled, its reminder task is cancelled and re-registered. If the item has a `customReminderTime` (`HH:mm`) the reminder fires at that clock time on the review date, otherwise at `nextReviewDate` itself. Pending reminders are restored on application startup.
- **Hourly sweep** (cron `0 0 * * * *`): creates a "Time to Review!" notification for users with due items, at most once per user every 24 hours.
- **Daily summary** (cron `0 0 8 * * *`): sends each user their due-item count at 08:00.
- **Delivery queue** (every 60 seconds): flushes notifications whose `deliverAt` timestamp has passed, moving them from `PENDING` to `DELIVERED`.
- **Transport**: every notification is persisted and then published to the STOMP topic `/topic/users/{userId}` (endpoint `/websocket`, SockJS fallback enabled).

---

## Testing & CI

- **Unit tests**: `AuthServiceTest`, `AuditLogServiceTest`, `VerificationTokenServiceTest`.
- **Integration tests**: `AuthIntegrationTest`, `UserIntegrationTest`, both bootstrapping the full application context against H2 with the `test` profile.
- **Continuous Integration**: GitHub Actions runs `mvn clean test` on every push and pull request targeting `main` or `develop`, using Temurin JDK 21 with Maven dependency caching and the `JWT_SECRET` repository secret.

---

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## License

Distributed under the MIT License. See LICENSE for more information.

---

Created by [Blessed Winner](https://github.com/blessed-winner)
