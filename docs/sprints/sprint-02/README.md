## Sprint 2: Database Integration & Movie CRUD
**Goal:** Transition from a hardcoded in-memory state to a persistent relational database, and build the base domain entity for movies.
**Key Deliverables:**
- Integration of Spring Data JPA and PostgreSQL Driver dependencies.
- Configuration of `application.yml` to connect to PostgreSQL (with environment variables for password security).
- Creation of the `Movie` JPA Entity (id, title, genre, duration).
- Implementation of `MovieRepository` (JPA interface).
- Implementation of `MovieController` exposing:
    - `POST /api/movies` (Create a movie).
    - `GET /api/movies` (Retrieve all movies).
- Introduction of Lombok (`@Data`, `@NoArgsConstructor`) to reduce boilerplate.
- Fix: Secured database credentials using `${POSTGRES_PASSWORD}` environment placeholders.

**Definition of Done (DoD):**
- Application starts successfully connected to PostgreSQL.
- Postman can send a `POST` request and return a movie with an auto-generated ID.
- `GET /api/movies` returns the inserted data.
- pgAdmin confirms the `movie` table exists with the inserted row.