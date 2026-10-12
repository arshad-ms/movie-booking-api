# Sprint 12.1: Historical Seat Data via BookingSeat Join Entity

**Goal:** Fix the bug where cancelled/expired bookings lost their seat history. Introduce a `BookingSeat` join entity that persists the booking↔seat link independently of `Seat.currentBooking`.

**Key Deliverables:**
- New `BookingSeat` entity (`booking`, `seat`, `priceAtBooking`, `status`).
- New `BookingSeatStatus` enum: `RESERVED`, `CONFIRMED`, `CANCELLED`, `EXPIRED`.
- New `BookingSeatRepository` with `findByBooking`.
- `Booking` entity updated with `@OneToMany(mappedBy = "booking", cascade = ALL, orphanRemoval = true)`.
- `BookingService.createBooking()` now persists `BookingSeat` rows and passes them to the mapper.
- `BookingService.cancelBooking()` and `expirePendingBookings()` update `BookingSeat.status` instead of deleting rows.
- `BookingMapper` signature changed to accept `List<BookingSeat>` instead of `List<Seat>`.
- All GET booking endpoints now correctly return historical seats for cancelled/expired bookings.

**Definition of Done (DoD):**
- Cancelled booking retains 3 seat records with `status = CANCELLED`.
- Expired booking retains seat records with `status = EXPIRED`.
- Live seat is released (`current_booking_id = NULL`, `status = AVAILABLE`).
- `GET /api/bookings/{id}` for any status returns populated `seats` array.
- No `LazyInitializationException` on any read path.

**Technical Decisions:**
- Chose a proper join entity over a denormalized `bookedSeatLabels` column — teaches the many-to-many-with-attributes pattern and is the industry standard.
- `Seat.currentBooking` retained for fast "currently reserved by" lookup. `BookingSeat` is the immutable historical record.
- `BookingSeatStatus` is distinct from `SeatStatus` (they model different lifecycles).

**Migration Notes:**
- Development DB was wiped (bookings + seats reset) because pre-existing booking_seat data could not be backfilled for cancelled/expired rows.

**Known Technical Debt:**
- `priceAtBooking` is hardcoded to `10.0`; will source from a real pricing model in a future sprint.
- No way to query "all bookings for a seat" yet — will be trivial once `BookingSeatRepository` is queried more.

**Code Review Notes:**
- Flagged: `@Data` on bidirectional entities (`Booking`, `BookingSeat`) risks StackOverflowError in toString/equals/hashCode. Fix scheduled as part of a code hygiene sprint.
- Flagged: N+1 query in paginated booking endpoints — fetch BookingSeat rows in batch via `findByBookingIdIn` in a follow-up sprint.
- Redundant explicit saves removed from @Transactional methods (relying on dirty checking).

**Known Technical Debt:**
- N+1 in `getMyBookings` and `getAllBookings` — batch fetch planned.
- `@Data` on entities with relationships — refactor to `@Getter/@Setter/@EqualsAndHashCode(of="id")` + `@ToString(exclude=...)`.
- `Theater` vs `Theatre` naming inconsistency still present.
- Field injection (`@Autowired`) still present.