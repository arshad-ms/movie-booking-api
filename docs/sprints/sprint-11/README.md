# Sprint 11: Admin Management Endpoints & Booking Lifecycle

**Goal:** Complete CRUD for movies (Update, Delete) and introduce booking retrieval and cancellation. Add pagination for list endpoints. Introduce ownership-based authorization.

**Key Deliverables:**
- `PUT /api/movies/{id}` — admin only, updates movie.
- `DELETE /api/movies/{id}` — admin only, rejects if showtimes exist (`MovieInUseException` → 409).
- `GET /api/bookings/me` — authenticated user's bookings, paginated.
- `GET /api/bookings/{id}` — owner or admin only.
- `GET /api/admin/bookings` — admin only, paginated.
- `DELETE /api/bookings/{id}` — owner or admin; releases seats; only PENDING/CONFIRMED cancellable.
- Created `PagedResponseDTO<T>` to avoid Spring's `PageImpl` serialization instability.
- Added `MovieInUseException` (409) and `BusinessRuleException` (409) with handlers in `GlobalExceptionHandler`.
- Added repository methods: `BookingRepository.findByUserId`, `ShowtimeRepository.existsByMovieId`.

**Definition of Done (DoD):**
- Admin can update/delete movies; user cannot.
- Owner can view/cancel own bookings; admin can view/cancel any.
- Non-owner regular user gets `403 Forbidden`.
- Cancellation releases seats (status → AVAILABLE, currentBooking → null).
- Cancelling an already cancelled/expired booking returns 400.
- List endpoints return stable `PagedResponseDTO` shape.

**Technical Decisions:**
- Ownership enforcement in service layer (Pattern A) rather than SpEL-based `@PreAuthorize` (Pattern B) — simpler, easier to test.
- Custom `PagedResponseDTO<T>` instead of Spring's `Page<T>` for API stability.
- New `BusinessRuleException` for state-transition violations (cancellation of CANCELLED booking), distinguished from `InvalidBookingException` (input format).

**Known Technical Debt:**
- Admin user still created manually via SQL; bootstrap mechanism deferred.
- Field injection (`@Autowired`) still present in services.
- `Theater` vs `Theatre` naming inconsistency persists.
- No `@Transactional(readOnly = true)` on read methods yet.
  **Known Bugs / Follow-ups:**
- Cancelled/expired bookings return `seats: []` because `Seat.currentBooking` is nulled during cancellation. Historical seat data is lost. Fix: introduce `BookingSeat` join entity (long-term) or `bookedSeatLabels` denormalized column (quick fix).
- `/api/bookings/me` returns all statuses. Add `?status=PENDING,CONFIRMED` filter in future sprint.

**Code Review Notes:**
- `BusinessRuleException` mapped to `400 Bad Request` (semantically closer to `409 Conflict`, but 400 is acceptable and consistent within API).
- `createPageable()` should be extracted to a shared utility if other services adopt pagination.
- `getMyBookings()` makes 1 extra DB call for user lookup; can be replaced with `findByUserEmail`.

**Known Bugs / Follow-ups:**
- Cancelled/expired bookings return `seats: []` because `Seat.currentBooking` is nulled during cancellation. Historical seat data is lost. Fix: introduce `BookingSeat` join entity (long-term) or `bookedSeatLabels` denormalized column (quick fix).
- `/api/bookings/me` returns all statuses. Add `?status=PENDING,CONFIRMED` filter in future sprint.

**Code Review Notes:**
- `BusinessRuleException` mapped to `400 Bad Request` (semantically closer to `409 Conflict`, but 400 is acceptable and consistent within API).
- `createPageable()` should be extracted to a shared utility if other services adopt pagination.
- `getMyBookings()` makes 1 extra DB call for user lookup; can be replaced with `findByUserEmail`.