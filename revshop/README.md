# RevShop

RevShop is a full-stack monolithic e-commerce application built with Spring Boot, Spring Data JPA, Spring Security, Thymeleaf, Oracle SQL, and Maven.

## Implemented Modules
- Authentication and role management (`BUYER`, `SELLER`)
- Product catalog: add, update, delete, search, category browse, seller inventory, low-stock checks
- Cart management: add/update/remove/clear with total calculation
- Checkout and orders: cart-to-order conversion with order items and stock updates
- Payment simulation: `CASH_ON_DELIVERY`, `CREDIT_CARD`, `DEBIT_CARD`
- Reviews and ratings (only for delivered purchased products)
- Favorites (buyer wishlist)
- In-app notifications for buyers and sellers
- Thymeleaf UI pages (`/`, `/login`, `/register/*`, `/dashboard`, `/dashboard/buyer`, `/dashboard/seller`)

## Tech Stack
- Java 21
- Spring Boot 4
- Spring Data JPA
- Spring Security
- Thymeleaf (HTML/CSS/JS)
- Oracle SQL (primary)
- H2 (test profile)
- Maven
- Git

## Run Locally
1. Configure Oracle DB in `src/main/resources/application.properties`.
2. From `revshop/`:
   ```bash
   ./mvnw spring-boot:run
   ```
3. Open `http://localhost:8080`.

## Test
- Tests run with isolated H2 profile:
  ```bash
  ./mvnw -q test
  ```

## API Highlights
- Auth: `/api/auth/**`
- Products: `/api/products/**`
- Cart: `/api/cart/**`
- Orders: `/api/orders/**`
- Payments: `/api/payments/**`
- Reviews: `/api/reviews/**`
- Favorites: `/api/favorites/**`
- Notifications: `/api/notifications/**`

## Documentation
- ERD: `docs/ERD.md`
- Architecture: `docs/ARCHITECTURE.md`
- Testing artifacts: `docs/TESTING.md`
- API endpoints: `docs/API_ENDPOINTS.md`
- Postman collection: `docs/postman/RevShop.postman_collection.json`

## CI
- GitHub Actions workflow: `.github/workflows/ci.yml` (runs Maven tests on pushes and PRs)

## Branching Workflow Used
- `feature/integration-complete` -> merged into `develop`
- `feature/favorites-ui-docs` -> merged into `develop`
- `feature/final-polish` -> merged into `develop`
- `feature/api-tests-polish` -> merged into `develop`
- `feature/controller-ui-flow` -> merged into `develop`
- `feature/final-completion` -> merged into `develop`
