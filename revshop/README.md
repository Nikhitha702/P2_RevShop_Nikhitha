# RevShop

RevShop is a full-stack monolithic e-commerce web application for buyers and sellers.

## Tech Stack
- Java 21
- Spring Boot 4.0.3
- Spring Security (session + role-based access)
- Spring Data JPA + Hibernate
- Thymeleaf + Bootstrap
- Oracle DB (runtime), H2 (tests)
- Maven

## Implemented Functional Scope
### Buyer
- Register/login
- Browse products by category
- Search products by keyword
- View product details (price, description, available quantity)
- Add/update/remove cart items
- View cart total
- Checkout with shipping and billing info
- Simulated payment (COD/Credit/Debit)
- Receive order confirmation + notifications
- View order history
- Save/remove favorites
- Submit and view product reviews/ratings (only for purchased products)

### Seller
- Register/login
- Add/update/delete products
- Add categories
- View inventory and low stock alerts
- Set discounted price and MRP
- View orders for their products
- Update order status
- Receive notifications for new orders and low stock
- View product reviews/ratings left by buyers

### Platform
- Authentication and account management
- Notification system
- Responsive web UI (storefront + role dashboards)
- Structured data model with validations and global API error handling

## Run
```bash
cd revshop
mvn spring-boot:run
```

App URL: `http://localhost:8080`

## Test
```bash
mvn test
```

## API Highlights
- `POST /api/auth/register/buyer`
- `POST /api/auth/register/seller`
- `GET /api/products/all`
- `POST /api/cart/add`
- `POST /api/orders/checkout`
- `POST /api/payments/pay`
- `POST /api/favorites/{productId}`
- `GET /api/favorites`
- `POST /api/reviews/product/{productId}`
- `GET /api/reviews/product/{productId}/summary`
- `GET /api/reviews/seller`

## Project Structure
- `src/main/java/com/revshop/config` - security and web config
- `src/main/java/com/revshop/controller` - MVC/API controllers
- `src/main/java/com/revshop/service` - business logic
- `src/main/java/com/revshop/repository` - JPA repositories
- `src/main/java/com/revshop/entity` - domain entities
- `src/main/resources/templates` - Thymeleaf pages
- `src/main/resources/static` - JS/CSS
- `src/test/java/com/revshop/service` - service unit tests

## Definition Of Done Artifacts
- ERD: [docs/ERD.md](docs/ERD.md)
- Architecture: [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)
- Testing Artifacts: [docs/TESTING.md](docs/TESTING.md)
