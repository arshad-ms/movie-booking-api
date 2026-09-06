# Sprint 5.3: JWT Token Generation & Stateless Authentication

**Goal:** Replace Basic Authentication with stateless JWT (JSON Web Token) authentication, enabling a scalable login mechanism where credentials are exchanged once for a token.

**Key Deliverables:**
- Added JJWT dependencies (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`).
- Created `JwtUtil` component for generating, validating, and extracting data from tokens (with HS256 signing).
- Created `AuthController` exposing `POST /api/auth/login` (accepts `email`/`password`, returns JWT).
- Created `JwtAuthenticationFilter` (extends `OncePerRequestFilter`) to intercept requests, validate Bearer tokens, and set the Security Context.
- Updated `SecurityConfig`:
  - Removed `.httpBasic()`.
  - Added `.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)`.
  - Permitted `/api/auth/login` to be public.

**Definition of Done (DoD):**
- Postman successfully receives a JWT token from `/api/auth/login`.
- The token is required for accessing `/api/movies`, `/api/bookings`, etc.
- Invalid/expired/missing tokens result in a `401 Unauthorized` error.
- Basic Auth no longer works (stateless).

**Technical Decisions:**
- Switched to JWT to support stateless, horizontally scalable deployments.
- Used HS256 algorithm (symmetric) for simplicity and performance.
- Stored the signing secret in environment variables to keep it secure.