# Sprint 12.3: Migrate to JPA Specifications (Approach B)

**Goal:** Replace the `@Query`-based filtering from Sprint 12.2 with composable JPA Specifications. Behavior identical; implementation mechanism improved.

**Key Deliverables:**
- `MovieRepository` and `BookingRepository` now extend `JpaSpecificationExecutor<T>`.
- Deleted `MovieRepository.searchMovies(...)` — entire @Query removed.
- Deleted `BookingRepository.findWithFilters(...)` — entire @Query removed.
- Deleted `normalizeStatuses()` from `BookingService`.
- New `MovieSpecification`:
  - `titleContains(search)` — case-insensitive LIKE, uses `Locale.ROOT`
  - `hasGenre(genre)` — case-insensitive exact match
- New `BookingSpecification`:
  - `belongsToUser(userId)`
  - `hasStatusIn(statuses)`
  - `bookingTimeBetween(from, to)` — handles from-only, to-only, both, neither
  - `hasMovieId(movieId)`
- All specs return `null` predicate when their input is not applicable → safe to combine via `Specification.allOf(...)`.
- Extracted `toPagedResponse(Page<Booking>)` helper in `BookingService` (DRY across 2 endpoints).
- Controllers **unchanged** — same params, same validation, same responses.

**Definition of Done (DoD):**
- Behavior identical to Sprint 12.2 across all acceptance tests.
- No `@Query`, no `CAST`, no `IS NULL OR`, no `normalizeStatuses()` in codebase.
- Each spec is a focused, testable unit.
- Ownership scoping preserved (`belongsToUser` always combined for `/me`).
- Pagination works with filters.

**Comparison — What Improved:**

| Aspect | 12.2 (@Query) | 12.3 (Specifications) |
| :--- | :--- | :--- |
| Query readability | One 5-filter query, 4 CAST workarounds | 4 small, named methods |
| Adding a filter | Edit query + retest all combinations | Add one method + one line in allOf |
| Null status handling | `normalizeStatuses()` materializes all enum values | `hasStatusIn` returns null predicate → skipped |
| Testability | Whole query tested as a unit | Each predicate testable in isolation |
| Reuse across /me & /admin | Shared query with null args | Compose only relevant specs per endpoint |
| Type safety | Strong (JPQL field check at startup) | Weaker (field names as strings) |
| Boilerplate per filter | ~5 lines within one query | ~6 lines in one method |

**Trade-off Accepted:**
- Lost compile-time type safety on field names (e.g., `root.get("status")` is a string — typos only fail at runtime).
- Gained far better composability, testability, and readability.

**Git History Evidence:**
- `git diff` shows `findWithFilters` and `searchMovies` fully deleted.
- New specification classes added with 6 total methods replacing 2 monolithic queries.

**Known Technical Debt (unchanged):**
- N+1 query in paginated booking endpoints (`toPagedResponse`).
- Field injection across services.
- `Theater`/`Theatre` naming inconsistency.
- `getBookingById` and `cancelBooking` do not share the defensive auth check from `getMyBookings` — extract `getCurrentUser()` helper in future sprint.
- Movie list endpoint not paginated.