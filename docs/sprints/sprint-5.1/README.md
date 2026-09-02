## Sprint 5.1: Security Lockdown (Basic Authentication)
**Goal:** Harden the API by introducing Spring Security. Ensure no endpoints are accessible without valid credentials, proving the Security Filter Chain works.
**Key Deliverables:**
- Added `spring-boot-starter-security` dependency.
- Created `SecurityConfig` class with `@EnableWebSecurity`.
- Configured HTTP Security to:
    - Disable CSRF (stateless REST API).
    - Require authentication for *all* requests (`anyRequest().authenticated()`).
    - Enable HTTP Basic Auth.
- Implemented an in-memory `UserDetailsService` with credentials (`admin` / `password`) using the `{noop}` password prefix.
- Verified that `POST /api/bookings` and `GET /api/movies` return `200 OK` with valid credentials, and `401 Unauthorized` without.

**Definition of Done (DoD):**
- Postman requests without Authorization header fail with `401`.
- Postman requests with Basic Auth (`admin:password`) succeed.
- No existing API functionality is broken; security acts as a protective wrapper.