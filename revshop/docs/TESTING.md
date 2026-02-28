# Testing Artifacts

## Current Automated Test
- `RevshopApplicationTests` (Spring context load)

## Validation Performed
- Application compiles and tests pass with:
  ```bash
  ./mvnw -q test
  ```
- JPA schema migration was validated against Oracle with `ddl-auto=update`.

## Suggested Additional Tests
1. Service unit tests for cart, checkout, payment, and review logic.
2. Repository tests for seller order queries and favorites uniqueness.
3. Controller integration tests for role-based authorization paths.
4. End-to-end test scenario:
   - Buyer registration -> login -> add to cart -> checkout -> payment -> review.
   - Seller login -> inventory view -> order status update -> low-stock alert.
