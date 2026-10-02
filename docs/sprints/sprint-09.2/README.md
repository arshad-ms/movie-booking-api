# Sprint 9.2: DTO Refactor for Showtime, Booking, Seat

**Goal:** Complete the DTO migration. Flatten deeply nested booking/showtime responses into frontend-friendly DTOs. Remove all entity exposure from controllers.

**Key Deliverables:**
- Created DTOs:
  - `ShowtimeRequestDTO`, `ShowtimeResponseDTO` (flattened, with `availableSeatCount`)
  - `SeatResponseDTO`
  - `BookingRequestDTO` (renamed from `BookingRequest`), `BookingResponseDTO` (flattened movie/theater/screen + nested seats list)
- Created mappers: `ShowtimeMapper`, `SeatMapper`, `BookingMapper` (all `@Component` for consistency).
- Extracted `ShowtimeService` from `ShowtimeController`.
- Refactored `BookingService.createBooking()` to return `BookingResponseDTO`.
- Added `SeatRepository.countByScreenIdAndStatus()`.
- Removed `@JsonIgnore` from `Booking.user` and `Screen.theater` (entities no longer exposed).
- Added `HttpMessageNotReadableException` handler → `400 Bad Request` (empty/malformed body).
- Added `SeatMapper.toResponseDTOList()`.

**Definition of Done (DoD):**
- No endpoint returns a JPA entity.
- Booking response contains flattened movie title, theater name, screen number, and nested seat list.
- Showtime response contains `availableSeatCount`.
- No infinite recursion in any JSON response.
- Empty/malformed request bodies return `400` (previously 500).

**Technical Decisions:**
- `BookingMapper.toResponseDTO()` accepts the seat list as a parameter, keeping the mapper stateless and eliminating lazy-loading risk.
- DTO construction occurs inside the `@Transactional` method to ensure lazy relationships are loaded.
- `@JsonIgnore` annotations retained only where the entity could still be touched by Jackson in edge cases.

**Known Technical Debt:**
- GET endpoints for bookings (user's bookings, admin view) — planned for Sprint 10+.
- `SeatResponseDTO.price` is a placeholder (`null`); populate when pricing is implemented.
- Field injection (`@Autowired`) still present — planned refactor.
- Naming consistency: `Theater` vs `Theatre` still mixed in older files.