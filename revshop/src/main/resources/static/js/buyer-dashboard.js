const buyerSearch = document.getElementById('buyerSearch');
let buyerCategory = 'all';
const logEl = document.getElementById('buyerApiLog');

if (buyerSearch) {
    buyerSearch.addEventListener('input', applyBuyerFilters);
}

function apiLog(payload) {
    if (!logEl) return;
    logEl.textContent = typeof payload === 'string' ? payload : JSON.stringify(payload, null, 2);
}

function csrfHeaders(includeJson = true) {
    const token = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content') || 'X-XSRF-TOKEN';
    const headers = includeJson ? { 'Content-Type': 'application/json' } : {};
    if (token) headers[header] = token;
    return headers;
}

async function apiCall(url, method = 'GET', body = null) {
    const opts = { method, headers: csrfHeaders(body !== null) };
    if (body !== null) opts.body = JSON.stringify(body);

    const res = await fetch(url, opts);
    const contentType = res.headers.get('content-type') || '';
    const data = contentType.includes('application/json') ? await res.json() : await res.text();
    apiLog(data);
    return { ok: res.ok, data };
}

function buyerFilterCategory(category) {
    buyerCategory = (category || 'all').toLowerCase();
    applyBuyerFilters();
}

function applyBuyerFilters() {
    const search = (buyerSearch?.value || '').toLowerCase();
    document.querySelectorAll('#buyerProductGrid > div').forEach(card => {
        const name = (card.dataset.name || '').toLowerCase();
        const category = (card.dataset.category || '').toLowerCase();
        const matchesSearch = !search || name.includes(search);
        const matchesCategory = buyerCategory === 'all' || category === buyerCategory;
        card.style.display = matchesSearch && matchesCategory ? '' : 'none';
    });
}

async function addToCart(productId) {
    const qtyInput = document.getElementById(`qty_${productId}`);
    const qty = qtyInput ? Number(qtyInput.value) : 1;
    await apiCall(`/api/cart/add?productId=${productId}&quantity=${qty}`, 'POST');
}

async function viewCart() {
    await apiCall('/api/cart');
}

async function cartTotal() {
    await apiCall('/api/cart/total');
}

async function checkout() {
    const shippingAddress = document.getElementById('shipAddress').value;
    const billingAddress = document.getElementById('billAddress').value;
    await apiCall('/api/orders/checkout', 'POST', { shippingAddress, billingAddress });
}

async function myOrders() {
    await apiCall('/api/orders/my-orders');
}

async function payOrder() {
    const orderId = document.getElementById('payOrderId').value;
    const method = document.getElementById('payMethod').value;
    await apiCall(`/api/payments/pay?orderId=${orderId}&method=${method}`, 'POST');
}

async function markNotification(id) {
    const res = await fetch(`/api/notifications/${id}/read`, {
        method: 'PUT',
        headers: csrfHeaders(false)
    });

    if (res.ok) {
        window.location.reload();
    }
}
