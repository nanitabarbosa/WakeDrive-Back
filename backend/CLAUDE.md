# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

---

# Working with the user

- Before making architectural changes, confirm the expected behavior with the user.
- Do not create unnecessary abstractions, interfaces or generic solutions unless they are required by the project.
- Maintain the existing project structure and conventions.
- Before removing or modifying existing functionality, explain the impact.
- After creating, modifying, or deleting files, always stage changes in git (`git add` / `git rm`).
- The user is learning Spring Boot. Prefer standard, well-known patterns over clever or unusual solutions.

---

# Project overview

WakeDrive Backend is a Spring Boot REST API developed for monitoring and managing drowsiness detection devices used by companies.

The platform allows companies to:

- Request access to the platform.
- Manage companies.
- Manage users.
- Manage vehicles.
- Manage drowsiness detection devices.
- Associate users, vehicles and devices.
- Receive and manage drowsiness alerts.
- Configure device behavior.
- Generate operational information and reports.

The backend communicates with the Angular frontend through REST APIs.

---

# Technology stack

## Backend

- Java 21
- Spring Boot 3.x
- Maven
- Spring Data JPA
- PostgreSQL
- Spring Security
- JWT Authentication
- Hibernate
- Bean Validation
- OpenAPI / Swagger

---

# Coding conventions

- Controllers never receive or return Entities directly. Use DTOs for every request/response shape.
- Naming: `XxxDTO` for response DTOs, `XxxRequestDTO` for request bodies when they differ from the response shape.
- Entity ↔ DTO conversion happens in the Service layer (not in the Controller).
- Layer order is strict: Controller → Service → Repository → Entity. A layer only talks to its direct neighbor.

---

# Documentation requirements

After completing each phase (Entities, Repositories, Services, Controllers, Security), generate a short markdown report in a `/docs` folder:

- `docs/01-entities.md`, `docs/02-repositories.md`, etc.
- Each report should include: what was created, key design decisions, and any assumptions made.
- Keep reports concise (bullet points, not essays).

---

# Current state

- Database: PostgreSQL, connected locally (migration to Supabase/Render planned for deployment).
- Implemented so far: (update this list as phases are completed)
- Not implemented yet: authentication/JWT, companies module, devices module, alerts module.

---

# Build and run commands

```bash
./mvnw clean install

./mvnw spring-boot:run

./mvnw test
```