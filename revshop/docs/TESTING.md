# Testing Artifacts

## Automated Test Coverage
- `RevshopApplicationTests` (context load using test profile)
- Service unit tests:
  - `CartServiceTest`
  - `OrderServiceTest`
  - `PaymentServiceTest`
  - `ReviewServiceTest`
- Controller/security integration test:
  - `RoleAccessControllerTest`

## Test Profile
- `src/test/resources/application-test.properties`
- H2 in-memory DB, Oracle mode
- `ddl-auto=create-drop`

## Commands
- Run all tests:
  ```bash
  ./mvnw -q test
  ```

## CI Verification
- GitHub Actions workflow `.github/workflows/ci.yml` executes `./mvnw -q test` on pushes/PRs.

## Manual E2E Scenarios (for demo)
1. Buyer: register -> login -> add to cart -> checkout -> payment -> review.
2. Seller: register -> login -> add product -> view seller orders -> update order status.
3. Notifications: verify buyer/seller receive updates for order and status changes.
