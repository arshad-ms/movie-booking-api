# Sprint 6: Replacing Hardcoded User with Authenticated Context

**Goal:** Remove the hardcoded `userId = 1L` and dynamically assign the currently authenticated user (from JWT) to new bookings, ensuring tickets are attributed to the actual purchaser.

**Key Deliverables:**
- Updated `Booking` entity: Replaced `Long userId` with `@ManyToOne User user` (foreign key `user_id`, nullable = false).
- Added `@JsonIgnore` to the `user` field in `Booking` to prevent password hash exposure and infinite recursion in JSON serialization.
- Updated `BookingService.createBooking()`:
  - Extracts the `Authentication` object from `SecurityContextHolder`.
  - Retrieves the `UserDetails` principal and extracts the email.
  - Fetches the full `User` entity from `UserRepository` by email.
  - Associates the fetched `User` with the new booking before persisting.

**Definition of Done (DoD):**
- JWT token is required to create a booking (401/403 without it).
- The `user_id` column in the `booking` table is populated with the actual ID of the authenticated user.
- Booking as `user@test.com` associates the booking with that user's ID.
- The API response does not expose the user's password hash (`@JsonIgnore` working).
- Application starts and the schema is updated automatically by Hibernate.

**Known Issues / Technical Debt:**
- Missing Bearer token returns `403 Forbidden` instead of `401 Unauthorized`. A custom `AuthenticationEntryPoint` will be added in a future sprint to distinguish "not authenticated" from "not authorized."

**Technical Decisions:**
- Used proper JPA `@ManyToOne` relationship instead of a primitive `Long` to enforce foreign key constraints at the database level.
- Leveraged `SecurityContextHolder` to access the authenticated principal, keeping the system fully stateless.