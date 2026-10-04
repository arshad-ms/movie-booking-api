# Sprint 10: Role-Based Access Control (RBAC)

**Goal:** Introduce `ROLE_ADMIN` and `ROLE_USER`. Lock down catalog management endpoints to admins, while allowing regular users to browse and book.

**Key Deliverables:**
- Enabled method-level security via `@EnableMethodSecurity` on `SecurityConfig`.
- Created admin user in DB with `ROLE_ADMIN`.
- Migrated all users' roles in DB to `ROLE_*` prefix for consistency with `.authorities()` in `CustomUserDetailsService`.
- Annotated admin endpoints with `@PreAuthorize("hasRole('ADMIN')")`:
  - `POST /api/movies`
  - `POST /api/theaters`
  - `POST /api/screens`
  - `POST /api/showtimes`
- Added `AccessDeniedException` handler to `GlobalExceptionHandler` (defensive fallback).
- Added `HttpMessageNotReadableException` handler → `400` for empty/malformed bodies.
- Verified `JwtAccessDeniedHandler` returns `403` with a JSON body for non-admin attempts.
- Confirmed user browsing/booking endpoints unaffected.

**Definition of Done (DoD):**
- User token receives `403` on all 4 admin endpoints with structured JSON.
- Admin token successfully creates resources across all management endpoints.
- No token → `401`; invalid token → `401`; valid non-admin token → `403`.
- Booking and browsing flows unaffected for regular users.

**Technical Decisions:**
- Chose method-level `@PreAuthorize` over URL-based `requestMatchers` for finer granularity and better readability.
- Standardized DB role format to `ROLE_USER` / `ROLE_ADMIN` and used `.authorities()` in `CustomUserDetailsService`.
- Kept `UserNotAuthenticatedException` handler as defensive fallback (main path is blocked by `JwtAuthenticationEntryPoint`).

**Known Technical Debt:**
- Admin user created manually via SQL; a bootstrap mechanism (CommandLineRunner or env-var-driven seed) is planned for a future sprint.
- Field injection (`@Autowired`) still present across services — refactor planned.
- Hash generation snippet in `main()` removed post-sprint (see Sprint 11 cleanup).

**Cleanup Items Applied:**
- Removed hash-printing from `MovieBookingApiApplication.main()`.
- Deleted `TheaterScreenMismatchException` (unused since Sprint 9.2).