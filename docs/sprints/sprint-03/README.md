## Sprint 3: Domain Expansion (Theatres, Screens, Seats, Showtimes)
**Goal:** Build the core relational data model required for a cinema. Introduce complex JPA relationships (`@OneToMany`, `@ManyToOne`) and generate physical seats dynamically.
**Key Deliverables:**
- Creation of `Theatre`, `Screen`, `Seat`, and `Showtime` entities.
- Implementation of `TheatreRepository`, `ScreenRepository`, and `ShowtimeRepository`.
- Dynamic seat generation logic (e.g., generating 50 seats (A1...A10, B1...) when a Screen is created).
- Controllers:
    - `POST /api/theatres`
    - `POST /api/screens?theatreId=X&screenNumber=Y&totalSeats=Z` (Auto-generates seats).
    - `POST /api/showtimes` (Links Movie and Screen with start/end times).
- **Hotfix:** Resolved infinite JSON recursion (bidirectional loop) by adding `@JsonIgnore` on the child side of relationships.

**Definition of Done (DoD):**
- Postman can create a Theatre, then a Screen (which creates 20+ seats), then a Showtime.
- pgAdmin shows foreign key constraints (`theatre_id`, `screen_id`) correctly linking the tables.