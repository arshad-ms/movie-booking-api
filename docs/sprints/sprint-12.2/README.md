# Sprint 12.2: Dynamic Filtering via @Query (Approach A - Baseline)

**Goal:** Implement dynamic filters for movies and bookings using JPQL `@Query` with nullable parameters. This is the baseline implementation to be migrated to JPA Specifications in Sprint 12.3, enabling a direct side-by-side comparison.

**Key Deliverables:**
- `MovieRepository.searchMovies(search, genre)`:
    - Uses `CAST(:search AS String) IS NULL OR LOWER(title) LIKE ...` pattern.
    - Case-insensitive via `LOWER(...) LIKE LOWER(...)`.
- `BookingRepository.findWithFilters(userId, statuses, fromTime, toTime, movieId, pageable)`:
    - Uses `CAST(:userId AS Long) IS NULL OR ...` to work around Hibernate 6's null-parameter typing issue.
    - Requires `statuses` to always be non-empty (`IN :statuses` without null check).
- Service normalization:
    - `normalizeFilter()` for movies (blank → null).
    - `normalizeStatuses()` for bookings (null/empty → `Arrays.asList(BookingStatus.values())`).
    - Date boundaries: `atStartOfDay()` / `atTime(LocalTime.MAX)` for inclusive range.
    - `fromDate > toDate` → `InvalidBookingException`.
- `MethodArgumentTypeMismatchException` handler → 400 with parameter name + value.

**Definition of Done (DoD):**
- All filters work on `/api/movies`, `/api/bookings/me`, `/api/admin/bookings`.
- Ownership scoping preserved (user filters never bypass user ID predicate).
- Invalid enum/date values return 400.
- Pagination works with filters.

**Pain Points Encountered (Motivation for 12.3):**
- **4 `CAST` workarounds** in a single query due to Hibernate 6's strict null-parameter typing. Not readable, not scalable.
- **`normalizeStatuses()` hack** — must materialize all enum values when client sends no filter. Inverts the semantic intent of the query.
- **Monolithic query** — adding a 6th filter means editing the query + re-testing every combination.
- **`/me` vs `/admin` share a query but with different null arg patterns** — intent invisible to readers.
- **Cannot unit-test individual filters** — the whole query must be tested as a unit.

**Known Technical Debt (unchanged):**
- N+1 query in paginated booking endpoints.
- `@Data` on bidirectional entities (fix applied in 12.1; verify).
- Field injection.
- `Theater`/`Theatre` naming inconsistency.