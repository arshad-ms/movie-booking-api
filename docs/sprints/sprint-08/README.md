# Sprint 8: User Registration (Signup) Endpoint

**Goal:** Allow new users to register via a public endpoint. Passwords are BCrypt-hashed, default role is `ROLE_USER`, and duplicate emails are rejected.

**Key Deliverables:**
- DTOs: `RegisterRequest`, `RegisterResponse` (with `message` field), `UserResponseDTO`.
- New `AuthService` class with:
  - `login(email, password)` — delegates to `AuthenticationManager`, returns JWT.
  - `register(email, password)` — checks duplicate email, hashes password, assigns `ROLE_USER`, saves.
- `AuthController`: Added `POST /api/auth/register` returning `201 Created`.
- `SecurityConfig`: Permitted `/api/auth/register` publicly.
- `UserAlreadyExistsException` mapped in `GlobalExceptionHandler` to `409 Conflict`.
- Jakarta validation added to `RegisterRequest` (`@NotBlank`, `@Email`, `@Size(min=8)`).
- `MethodArgumentNotValidException` handled globally → `400 Bad Request`.

**Definition of Done (DoD):**
- Registration hashes passwords with BCrypt.
- Newly registered user can log in with `/api/auth/login`.
- Duplicate email returns `409 Conflict`.
- Empty/invalid body returns `400 Bad Request` with a field-specific message.
- Public endpoints `/api/auth/register` and `/api/auth/login` bypass authentication.
- Role is stored as `ROLE_USER` in the DB and loaded via `.authorities()` in `CustomUserDetailsService`.

**Technical Decisions:**
- Separated registration logic into `AuthService` (business logic) vs `CustomUserDetailsService` (Spring Security integration hook).
- Logout is intentionally not implemented: JWT is stateless, and logout is a client-side operation. Token blacklist (via Redis) deferred to a future sprint.
- Switched from `.roles()` (which auto-prefixes `ROLE_`) to `.authorities()` for explicit, self-documenting role handling.
- `RegisterResponse` intentionally uses `message` field name to avoid confusion with sensitive `password`.

**Known Technical Debt:**
- AuthService still uses field injection (`@Autowired`) — to be refactored to constructor injection in a future sprint.
- Logout / token revocation not implemented.