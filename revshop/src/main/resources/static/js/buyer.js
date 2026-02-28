window.addEventListener('DOMContentLoaded', () => {
    revshop.setLog('buyerLog');
});

async function addToCart() {
    const productId = document.getElementById('cartProductId').value;
    const quantity = document.getElementById('cartQty').value;
    await revshop.callApi(`/api/cart/add?productId=${productId}&quantity=${quantity}`, { method: 'POST' });
}

async function viewCart() {
    await revshop.callApi('/api/cart');
}

async function updateCartItem() {
    const itemId = document.getElementById('cartItemId').value;
    const quantity = document.getElementById('cartItemQty').value;
    await revshop.callApi(`/api/cart/update?cartItemId=${itemId}&quantity=${quantity}`, { method: 'PUT' });
}

async function removeCartItem() {
    const itemId = document.getElementById('cartItemId').value;
    await revshop.callApi(`/api/cart/remove?cartItemId=${itemId}`, { method: 'DELETE' });
}

async function cartTotal() {
    await revshop.callApi('/api/cart/total');
}

async function checkout() {
    const shippingAddress = document.getElementById('shipAddress').value;
    const billingAddress = document.getElementById('billAddress').value;
    await revshop.callApi('/api/orders/checkout', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ shippingAddress, billingAddress })
    });
}

async function myOrders() {
    await revshop.callApi('/api/orders/my-orders');
}

async function payOrder() {
    const orderId = document.getElementById('payOrderId').value;
    const method = document.getElementById('payMethod').value;
    await revshop.callApi(`/api/payments/pay?orderId=${orderId}&method=${method}`, { method: 'POST' });
}

async function addFavorite() {
    const productId = document.getElementById('favProductId').value;
    await revshop.callApi(`/api/favorites/${productId}`, { method: 'POST' });
}

async function removeFavorite() {
    const productId = document.getElementById('favProductId').value;
    await revshop.callApi(`/api/favorites/${productId}`, { method: 'DELETE' });
}

async function listFavorites() {
    await revshop.callApi('/api/favorites');
}

async function addReview() {
    const productId = document.getElementById('reviewProductId').value;
    const rating = document.getElementById('reviewRating').value;
    const comment = encodeURIComponent(document.getElementById('reviewComment').value);
    await revshop.callApi(`/api/reviews/add?productId=${productId}&rating=${rating}&comment=${comment}`, { method: 'POST' });
}

async function listNotifications() {
    await revshop.callApi('/api/notifications');
}

async function markRead() {
    const id = document.getElementById('noteId').value;
    await revshop.callApi(`/api/notifications/${id}/read`, { method: 'PUT' });
}
