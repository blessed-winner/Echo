# Echo - Spaced Repetition Memory Assistant

Echo is a powerful backend service built with Spring Boot, designed to help users retain knowledge through an optimized Spaced Repetition System (SRS). It manages notes, topics, and "memory items" with a built-in scheduling algorithm to ensure you review what matters, exactly when you need to.

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

- **Spaced Repetition Engine**: Uses a sophisticated algorithm (Ease Factor, Intervals, and Review Counts) to calculate the next optimal review date.
- **Structured Organization**: Organize your learning into Topics, Notes, and Memory Items.
- **Tagging System**: Flexible tagging for cross-referencing and filtering content.
- **Secure Authentication**: Robust JWT-based security with Access and Refresh token support.
- **Progress Tracking**: Real-time statistics including daily review counts, overdue items, and learning streaks.
- **API Documentation**: Fully documented with Swagger UI (Dark Mode enabled).

---

## Tech Stack

- **Backend**: Java 21, Spring Boot 3.4
- **Security**: Spring Security, JWT (JJWT)
- **Database**: PostgreSQL
- **Migrations**: Flyway
- **Mapping**: MapStruct
- **Documentation**: Springdoc OpenAPI (Swagger)
- **Deployment**: Docker, Render-ready

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
   cd Echo
   ```

2. **Configure Environment Variables**:
   Create a .env file in the root directory or set the following variables:
   ```env
   DB_URL=jdbc:postgresql://localhost:5432/echo
   DB_USERNAME=your_username
   DB_PASSWORD=your_password
   JWT_SECRET=your_super_secret_high_entropy_key_here
   ```

3. **Build and Run**:
   ```bash
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```

4. **Access Swagger UI (Local)**:
   Open http://localhost:8080/swagger-ui/index.html to explore the API locally.

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

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/auth/register` | Create a new account |
| `POST` | `/auth/login` | Obtain JWT tokens |
| `GET` | `/memories/due` | Get items pending review |
| `POST` | `/memories/{id}/review` | Submit a review (Again, Hard, Good, Easy) |
| `GET` | `/memories/stats` | Get user learning statistics |
| `GET` | `/topics` | List all learning topics |

---

## Security

The application uses JWT Authentication.
1. Obtain a token via /auth/login.
2. Include the token in the header of subsequent requests:
   Authorization: Bearer <your_access_token>

---

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the Project
2. Create your Feature Branch (git checkout -b feature/AmazingFeature)
3. Commit your Changes (git commit -m 'Add some AmazingFeature')
4. Push to the Branch (git push origin feature/AmazingFeature)
5. Open a Pull Request

---

## License

Distributed under the MIT License. See LICENSE for more information.

---

Created by [Blessed Winner](https://github.com/blessed-winner)
