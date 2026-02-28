# API Endpoints

Base URL: `http://localhost:8080`

## Auth
- `POST /api/auth/register/buyer` (public)
- `POST /api/auth/register/seller` (public)
- `POST /api/auth/login?email=&password=` (public)
- `POST /api/auth/logout` (authenticated)

## Products
- `GET /api/products` (public)
- `GET /api/products/{id}` (public)
- `GET /api/products/search?keyword=` (public)
- `GET /api/products/category/{name}` (public)
- `POST /api/products` (SELLER)
- `PUT /api/products/{id}` (SELLER)
- `DELETE /api/products/{id}` (SELLER)
- `GET /api/products/inventory` (SELLER)
- `GET /api/products/inventory/low-stock` (SELLER)

## Cart (BUYER)
- `POST /api/cart/add?productId=&quantity=`
- `GET /api/cart`
- `PUT /api/cart/update?cartItemId=&quantity=`
- `DELETE /api/cart/remove?cartItemId=`
- `GET /api/cart/total`
- `DELETE /api/cart/clear`

## Orders
- `POST /api/orders/checkout` (BUYER)
- `GET /api/orders/my-orders` (BUYER)
- `GET /api/orders/seller-orders` (SELLER)
- `PUT /api/orders/update-status?orderId=&status=SHIPPED|DELIVERED` (SELLER)

## Payments (BUYER)
- `POST /api/payments/pay?orderId=&method=CASH_ON_DELIVERY|CREDIT_CARD|DEBIT_CARD`

## Reviews
- `POST /api/reviews/add?productId=&rating=&comment=` (BUYER)
- `GET /api/reviews/product/{productId}` (public)

## Favorites (BUYER)
- `POST /api/favorites/{productId}`
- `DELETE /api/favorites/{productId}`
- `GET /api/favorites`

## Notifications
- `GET /api/notifications` (authenticated)
- `PUT /api/notifications/{notificationId}/read` (authenticated)

## Response Format
Mutating endpoints return:
```json
{
  "success": true,
  "message": "..."
}
```
