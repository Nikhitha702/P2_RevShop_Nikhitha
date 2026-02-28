# ERD

```mermaid
erDiagram
    USERS ||--o{ USER_ROLES : has
    ROLES ||--o{ USER_ROLES : mapped
    USERS ||--o| SELLERS : owns
    SELLERS ||--o{ PRODUCTS : lists
    CATEGORIES ||--o{ PRODUCTS : groups
    USERS ||--o| CARTS : owns
    CARTS ||--o{ CART_ITEMS : contains
    PRODUCTS ||--o{ CART_ITEMS : added
    USERS ||--o{ ORDERS : places
    ORDERS ||--o{ ORDER_ITEMS : contains
    PRODUCTS ||--o{ ORDER_ITEMS : ordered
    ORDERS ||--o{ PAYMENTS : paid_by
    USERS ||--o{ REVIEWS : writes
    PRODUCTS ||--o{ REVIEWS : receives
    USERS ||--o{ FAVORITES : saves
    PRODUCTS ||--o{ FAVORITES : favorited
    USERS ||--o{ NOTIFICATIONS : receives
```

## Key Entities
- `users`: buyer/seller auth and profile data
- `sellers`: business profile for seller users
- `products`: catalog with stock and threshold
- `orders` and `order_items`: checkout and fulfillment
- `payments`: simulated payment records
- `favorites`: buyer wishlist mapping
- `notifications`: in-app alerts
