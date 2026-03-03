const cartListEl = document.getElementById('cartList');
const cartTotalEl = document.getElementById('cartTotalValue');

function escapeHtml(value) {
    return String(value ?? '')
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#39;');
}

function safeInt(value, fallback = 0) {
    const parsed = Number.parseInt(value, 10);
    return Number.isFinite(parsed) ? parsed : fallback;
}

function safeNumber(value, fallback = 0) {
    const parsed = Number(value);
    return Number.isFinite(parsed) ? parsed : fallback;
}

async function viewCart() {
    const res = await apiCall('/api/cart');
    if (!res.ok || !Array.isArray(res.data) || !cartListEl) {
        return;
    }

    if (res.data.length === 0) {
        cartListEl.innerHTML = '<div class="text-muted small">Your cart is empty.</div>';
        return;
    }

    cartListEl.innerHTML = res.data.map((item) => `
        <div class="panel-card mb-2">
            <div class="fw-semibold"><i class="bi bi-bag me-1"></i>${escapeHtml(item.product?.name || 'Product')}</div>
            <div class="small"><i class="bi bi-cash-coin me-1"></i>INR ${safeNumber(item.product?.discountedPrice ?? item.product?.mrp, 0)}</div>
            <div class="small mb-2"><i class="bi bi-123 me-1"></i>${safeInt(item.quantity, 1)}</div>
            <div class="d-flex gap-2">
                <input id="cart_qty_${safeInt(item.id, 0)}" type="number" min="1" value="${safeInt(item.quantity, 1)}" class="form-control form-control-sm" style="max-width:140px;">
                <button class="btn btn-sm btn-outline-primary" onclick="updateCartItem(${safeInt(item.id, 0)})" title="Update"><i class="bi bi-arrow-repeat"></i></button>
                <button class="btn btn-sm btn-outline-danger" onclick="removeCartItem(${safeInt(item.id, 0)})" title="Remove"><i class="bi bi-trash3"></i></button>
            </div>
        </div>
    `).join('');
}

async function cartTotal() {
    const res = await apiCall('/api/cart/total');
    if (!res.ok || cartTotalEl == null) {
        return;
    }
    if (typeof res.data === 'number') {
        cartTotalEl.textContent = `INR ${res.data}`;
    }
}

async function updateCartItem(cartItemId) {
    const qty = Number(document.getElementById(`cart_qty_${cartItemId}`)?.value);
    if (!Number.isInteger(qty) || qty <= 0) {
        showToast('Enter valid quantity before update', 'error');
        return;
    }
    await apiCall(`/api/cart/update?cartItemId=${cartItemId}&quantity=${qty}`, 'PUT');
    await viewCart();
    await cartTotal();
}

async function removeCartItem(cartItemId) {
    await apiCall(`/api/cart/remove?cartItemId=${cartItemId}`, 'DELETE');
    await viewCart();
    await cartTotal();
}

document.addEventListener('DOMContentLoaded', async () => {
    await viewCart();
    await cartTotal();
});
