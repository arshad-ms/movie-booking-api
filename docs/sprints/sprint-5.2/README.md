# Sprint 5.2: Database-Backed Authentication & BCrypt Password Encoding

**Goal:** Replace the hardcoded in-memory user with a persistent `User` entity stored in PostgreSQL, and upgrade password security from plaintext (`{noop}`) to BCrypt hashing.

**Key Deliverables:**
- Creation of the `User` JPA Entity (`id`, `email`, `password`, `role`) with unique constraint on `email`.
- Implementation of `UserRepository` with a custom `findByEmail(String email)` method.
- Implementation of `CustomUserDetailsService` implementing Spring Security's `UserDetailsService` interface:
    - Overrides `loadUserByUsername(String username)` to fetch the user from PostgreSQL via `UserRepository`.
    - Converts the `User` entity into Spring Security's `UserDetails` object using the builder pattern.
- Update `SecurityConfig`:
    - Removed the `InMemoryUserDetailsManager` bean.
    - Introduced a `BCryptPasswordEncoder` bean.
    - Configured `AuthenticationManager` to use `CustomUserDetailsService` and `BCryptPasswordEncoder`.
- Manual insertion of a test user into the `users` table with a BCrypt-encoded password (using a temporary `main()` method print statement).

**Definition of Done (DoD):**
- Application starts successfully without any in-memory user configuration.
- The `admin` / `password` credentials (from Sprint 5.1) no longer work.
- Postman (Basic Auth) with `user@test.com` / `password123` returns `200 OK` for protected endpoints.
- Postman with invalid credentials returns `401 Unauthorized`.
- pgAdmin confirms the `users` table exists with the test user inserted and the password stored as a BCrypt hash (starts with `$2a$10$`).

**Technical Decisions:**
- Chose `BCryptPasswordEncoder` over `{noop}` for production-grade security (industry standard).
- Used `@Column(unique = true, nullable = false)` on `email` to enforce uniqueness at the database level.
- Leveraged Spring Security's `User.builder()` to construct `UserDetails` from the entity, ensuring compatibility with the authentication framework.