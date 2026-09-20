# Sprint 7: Custom Exceptions & HTTP Status Codes

**Goal:** Replace generic `RuntimeException` errors with meaningful custom exceptions mapped to correct HTTP status codes. Standardize error responses and fix the 401/403 authentication inconsistency.

**Key Deliverables:**
- Created `ErrorResponse` DTO (timestamp, status, error, message, path) for consistent error payloads.
- Created custom exceptions:
  - `ResourceNotFoundException` → 404
  - `SeatUnavailableException` → 409
  - `InvalidBookingException` → 400
  - `TheaterScreenMismatchException` → 400
  - `UserNotAuthenticatedException` → 401
- Expanded `GlobalExceptionHandler` to map each exception to its HTTP status and return `ErrorResponse`.
- Added a catch-all handler for `Exception.class` returning 500 with JSON body (plus logging).
- Refactored `BookingService` to throw custom exceptions instead of `RuntimeException`.
- Created `JwtAuthenticationEntryPoint` to return structured `401 Unauthorized` JSON for missing/invalid tokens.
- Created `JwtAccessDeniedHandler` to return structured `403 Forbidden` JSON for future role-based checks.
- Registered both handlers in `SecurityConfig` via `.exceptionHandling(...)`.

**Definition of Done (DoD):**
- Missing Bearer token returns `401 Unauthorized` with JSON (previously 403).
- Invalid/expired tokens return `401 Unauthorized` with JSON.
- Non-existent resources return `404 Not Found` with a descriptive message.
- Double-booking returns `409 Conflict`.
- Invalid input returns `400 Bad Request`.
- Unhandled exceptions return `500` with a consistent JSON error structure AND are logged with stack traces.

**Known Technical Debt:**
- Future sprints may introduce method-level security (`@PreAuthorize`) which will require adding an `AccessDeniedException` handler to `GlobalExceptionHandler`.

**Technical Decisions:**
- Centralized error responses in `ErrorResponse` DTO for frontend consistency.
- Used Spring Security's `AuthenticationEntryPoint` and `AccessDeniedHandler` to ensure security-layer errors bypass the standard filter chain and return structured JSON.
- Adopted the Boy Scout Rule: handled related technical debt (401 vs 403) within the same sprint as the error handling theme.
- Logging levels: `WARN` for client-side errors (4xx), `ERROR` with stack trace for server-side errors (5xx).