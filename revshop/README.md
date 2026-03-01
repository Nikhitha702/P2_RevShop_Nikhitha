# RevShop (Clean Rebuild)

This repository was reset and rebuilt with a clean branch strategy.

## Stack
- Java 21
- Spring Boot 4.0.3
- Spring Security (session auth)
- Spring Data JPA
- Thymeleaf + Bootstrap
- Oracle SQL (ojdbc11)
- Maven

## Current milestone (feature/user)
- Buyer and Seller registration
- Login/logout with session-based security
- Role-based routing foundation
- Oracle datasource configuration

## Run
```bash
cd revshop
mvn spring-boot:run
```

## Default branches
- `main`
- `develop`
- feature branches merged into `develop`, then promoted to `main`
