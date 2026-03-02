# RevShop ERD

```mermaid
erDiagram
    APP_USERS ||--o{ PRODUCTS : sells
    APP_USERS ||--o{ CART_ITEMS : owns
    APP_USERS ||--o{ ORDERS : places
    APP_USERS ||--o{ NOTIFICATIONS : receives
    APP_USERS ||--o{ FAVORITES : bookmarks
    APP_USERS ||--o{ PRODUCT_REVIEWS : writes

    CATEGORIES ||--o{ PRODUCTS : classifies
    PRODUCTS ||--o{ CART_ITEMS : added_to
    PRODUCTS ||--o{ ORDER_ITEMS : purchased_as
    PRODUCTS ||--o{ FAVORITES : favorited
    PRODUCTS ||--o{ PRODUCT_REVIEWS : reviewed

    ORDERS ||--o{ ORDER_ITEMS : contains
    ORDERS ||--|| PAYMENTS : paid_by

    APP_USERS {
      bigint id PK
      string first_name
      string last_name
      string email UK
      string password
      string phone
      string address
      string role
      string business_name
      string gst_number
      string business_category
      boolean enabled
    }

    CATEGORIES {
      bigint id PK
      string name UK
      string description
      string image_url
      boolean active
    }

    PRODUCTS {
      bigint id PK
      string name
      string description
      decimal mrp
      decimal discounted_price
      int quantity
      int inventory_threshold
      string image_url
      boolean active
      timestamp created_at
      bigint category_id FK
      bigint seller_id FK
    }

    CART_ITEMS {
      bigint id PK
      int quantity
      bigint buyer_id FK
      bigint product_id FK
    }

    ORDERS {
      bigint id PK
      string status
      decimal total_amount
      string shipping_address
      string billing_address
      timestamp created_at
      bigint buyer_id FK
    }

    ORDER_ITEMS {
      bigint id PK
      int quantity
      decimal unit_price
      bigint order_id FK
      bigint product_id FK
      bigint seller_id FK
    }

    PAYMENTS {
      bigint id PK
      string method
      string status
      timestamp paid_at
      bigint order_id FK UK
    }

    NOTIFICATIONS {
      bigint id PK
      string message
      boolean read_status
      timestamp created_at
      bigint user_id FK
    }

    FAVORITES {
      bigint id PK
      timestamp created_at
      bigint buyer_id FK
      bigint product_id FK
    }

    PRODUCT_REVIEWS {
      bigint id PK
      int rating
      string review_text
      timestamp created_at
      bigint buyer_id FK
      bigint product_id FK
    }
```
