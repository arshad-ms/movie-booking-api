# Sprint 9.1: DTO Refactor for Movie, Theatre, Screen

**Goal:** Stop returning JPA entities from Movie, Theatre, and Screen endpoints. Introduce request/response DTOs, add Jakarta validation, and centralize mapping logic in dedicated mapper classes.

**Key Deliverables:**
- Renamed `MovieDTO` → `MovieResponseDTO` for naming consistency.
- Created DTOs:
  - `MovieRequestDTO`, `MovieResponseDTO`
  - `TheatreRequestDTO`, `TheatreResponseDTO` (with screenCount)
  - `ScreenRequestDTO`, `ScreenResponseDTO` (with flattened theatre info and seatCount)
- Added `spring-boot-starter-validation` dependency.
- Annotated request DTOs with `@NotBlank`, `@NotNull`, `@Positive`.
- Created dedicated mappers in `mapper` package: `MovieMapper`, `TheatreMapper`, `ScreenMapper`.
- Deleted `MapperUtil` (superseded by dedicated mappers).
- Refactored services to accept DTOs and return DTOs.
- Refactored controllers to use `@Valid @RequestBody` and return response DTOs.
- Converted `POST /api/screens` from query parameters to JSON body (breaking change, correct REST design).

**Definition of Done (DoD):**
- No endpoint returns a JPA entity directly.
- Invalid request bodies return `400 Bad Request` with a field-specific message.
- `ScreenResponseDTO` includes flattened `theatreName` instead of nested `theatre` object.
- `MethodArgumentNotValidException` handled globally.

**Breaking Changes:**
- `POST /api/screens` now expects JSON body instead of query parameters.
- Response shapes for Movie/Theatre/Screen endpoints changed (no nested entities, no `version` field).

**Technical Decisions:**
- Separate `mapper` package for maintainability; MapStruct considered but deferred for simplicity.
- Kept `@JsonIgnore` on entities as a temporary safeguard during transition. Full cleanup in Sprint 9.2.

**Technical Debt Backlog:**
- Lazy loading warning: `ScreenMapper.toResponseDTO()` accesses `seats.size()`. When GET endpoints are added, wrap service methods in `@Transactional` or use `SeatRepository.countByScreenId()`.
- Field injection (`@Autowired`) throughout codebase — refactor to constructor injection (planned Sprint 11 or later).
- Input validation on `ScreenRequestDTO` (min seats, screen number positive) — partially done.