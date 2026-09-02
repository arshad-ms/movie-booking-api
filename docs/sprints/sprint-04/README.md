## Sprint 4: Core Booking Logic & Concurrency Control (The "Heart" of the System)
**Goal:** Implement the seat reservation logic, ensuring data consistency even if two users attempt to book the same seat simultaneously.
**Key Deliverables:**
- Creation of `Booking` Entity (id, userId, showtime, bookingTime, status [PENDING/EXPIRED/CONFIRMED], totalPrice, expiresAt).
- Addition of `@Version` field to `Seat` entity for Optimistic Locking.
- Implementation of `BookingService` with `@Transactional` logic:
    - Validates seat existence and screen match.
    - Rejects duplicate seat IDs (`[1,2,2]`).
    - Checks seat status (must be `AVAILABLE`).
    - Saves the `Booking` first (to generate ID), then updates `Seat` status to `RESERVED` and links `currentBooking` foreign key.
- Scheduled method (`@Scheduled`) to run every 60 seconds, expiring `PENDING` bookings and releasing seats.
- Global Exception Handler for `ObjectOptimisticLockingFailureException` returning HTTP `409 Conflict`.

**Definition of Done (DoD):**
- Postman successfully creates a booking with `status: PENDING`.
- Attempting to book the same seat twice returns a `409 Conflict`.
- Duplicate seat IDs in the request are explicitly rejected.
- Scheduled job correctly resets expired seats to `AVAILABLE`.