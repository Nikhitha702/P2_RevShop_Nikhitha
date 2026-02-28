# Architecture

## Style
RevShop is implemented as a monolithic Spring Boot application with layered architecture:
- Controller layer (`controller`): REST + Thymeleaf endpoints
- Service layer (`service`): business logic and transaction boundaries
- Repository layer (`repository`): JPA persistence access
- Domain layer (`entity`, `dto`): model and request payloads

## Security
- Spring Security with role-based authorization
- Basic authentication and form login enabled
- Method-level access control via `@PreAuthorize`

## Data Flow
1. Buyer or seller authenticates.
2. Buyer browses products, manages cart, and checks out.
3. Checkout converts cart items to order items, updates stock, emits notifications.
4. Buyer pays with simulated method.
5. Seller monitors inventory and updates order status.

## UI
- Thymeleaf templates for core pages:
  - `/` product showcase
  - `/dashboard` notifications
- REST APIs support full CRUD and integration flows.
