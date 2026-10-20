# Sprint 13: Code Hygiene & Quality

**Goal:** No new features. Pay down accumulated technical debt to reach production-quality polish.

**Key Deliverables:**

**1. Dead Code Removal**
- Removed unused imports from `BookingRepository`, `MovieRepository`.
- Deleted `BookingRepository.findByUserId(...)` (replaced by `belongsToUser` spec).
- Removed all commented-out code in `BookingService`.
- Removed hash-printing from `MovieBookingApiApplication.main()`.
- Optimized imports project-wide.

**2. Naming Consistency (Theatre → Theater)**
- Renamed all classes, variables, methods for American spelling.
- Verified HTTP paths unchanged (`/api/theaters`).
- DB column names verified (`theater_id`).

**3. Constructor Injection**
- Replaced all `@Autowired` field injection with `private final` + Lombok `@RequiredArgsConstructor` across all services, controllers, and components.
- `SecurityConfig` refactored to use `@Bean` method parameters (no field injection).

**4. Security Utilities Extraction**
- Created `SecurityUtils` component:
  - `getCurrentUser()` — thorough auth check + user lookup
  - `isCurrentUserAdmin()` — role check
- Removed duplicate auth logic from `BookingService` (was repeated in 3 methods).
- `SecurityContextHolder` accessed only in `SecurityUtils` and `JwtAuthenticationFilter` (the latter is by design).

**5. N+1 Query Fix (Partial)**
- Added `BookingSeatRepository.findByBookingIdIn(List<Long>)`.
- Batch-fetch seats in `toPagedResponse()` via `Collectors.groupingBy`.
- Query count for page-10 bookings: ~41 → ~32 (booking→bookingSeat fixed; seat→proxy still per-seat).

**6. Read-Only Transactions**
- Migrated from `jakarta.transaction.Transactional` to `org.springframework.transaction.annotation.Transactional` (supports `readOnly`).
- Applied `@Transactional(readOnly = true)` to:
  - `BookingService.getMyBookings`, `getBookingById`, `getAllBookings`
  - `MovieService.getMovies`
  - `ShowtimeService.getSeatsByShowtime`

**7. Entity `@Data` Fix**
- Replaced `@Data` with `@Getter/@Setter/@EqualsAndHashCode(onlyExplicitlyIncluded=true, includes id)/@ToString(exclude=relationships)` on `Booking` and `BookingSeat`.

**Definition of Done (DoD):**
- ✅ All Postman tests pass unchanged.
- ✅ Zero `@Autowired` fields.
- ✅ Zero `Theatre` occurrences.
- ✅ Zero commented-out code.
- ✅ `SecurityContextHolder` centralized in `SecurityUtils`.
- ✅ Read methods use `readOnly = true`.
- ✅ `Booking`/`BookingSeat` no longer recurse in `toString`/`equals`.

**Known Technical Debt (new items):**
- `@Data` still present on `Screen`, `Seat`, `Showtime`, `Theater`. Latent recursion risk in `toString`/`equals`/`hashCode`. Fix in a future hygiene pass.
- Partial N+1: `BookingSeat.seat` still triggers lazy load per seat. Fix via `JOIN FETCH` in `findByBookingIdIn`.
- Two dead repository methods still present (`SeatRepository.findByCurrentBooking`, `BookingSeatRepository.findByBookingId`) — cleaned in follow-up.
- `LoginRequest` DTO has no validation annotations (`@Email`, `@NotBlank`).
- Logout / token revocation not implemented.
- Pricing hardcoded to `10.0`.
- Movie list endpoint not paginated.

**Why This Sprint Matters:**
- **Testability**: Services can be unit-tested with `new Service(mockDeps...)` without Spring context.
- **Fail-fast**: Missing beans crash at startup, not first-request.
- **Maintainability**: Single source of truth for auth logic.
- **Intent clarity**: `readOnly = true` documents that a method doesn't write.