# Testing Artifacts

## Automated
- Frameworks: JUnit 5, Mockito, Spring Boot Test
- Command:
```bash
mvn test
```

## Covered Service Areas
- Auth registration success/failure
- Product add flow
- Cart add flow and stock over-allocation prevention
- Notification creation/read
- Category creation
- Payment flow + duplicate payment prevention
- Favorites add flow
- Product review submission rules (purchased vs non-purchased)
- Spring context load

## Manual Functional Checklist
1. Register buyer and seller accounts
2. Login as seller, add category and product
3. Login as buyer, browse/search/filter products
4. Add to cart, update cart, remove cart item
5. Checkout and verify order creation
6. Pay with each payment method
7. Verify buyer/seller notifications
8. Add/remove favorites and list favorites
9. Submit review for purchased product
10. Verify seller can see product reviews
