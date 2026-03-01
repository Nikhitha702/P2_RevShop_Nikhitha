const buyerSearch = document.getElementById('buyerSearch');
let buyerCategory = 'all';
const logEl = document.getElementById('buyerApiLog');
const categoryWrap = document.getElementById('buyerCategories');
const productGrid = document.getElementById('buyerProductGrid');

if (buyerSearch) {
    buyerSearch.addEventListener('input', loadProducts);
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
    loadProducts();
}

function chipClass(active) {
    return active ? 'category-chip active' : 'category-chip';
}

function renderCategoryChips(categories) {
    if (!categoryWrap) return;
    categoryWrap.innerHTML = '';

    const allBtn = document.createElement('button');
    allBtn.className = chipClass(buyerCategory === 'all');
    allBtn.textContent = 'All';
    allBtn.onclick = () => buyerFilterCategory('all');
    categoryWrap.appendChild(allBtn);

    categories.forEach((cat) => {
        const btn = document.createElement('button');
        const key = (cat.name || '').toLowerCase();
        btn.className = chipClass(buyerCategory === key);
        btn.textContent = cat.name;
        btn.onclick = () => buyerFilterCategory(cat.name);
        categoryWrap.appendChild(btn);
    });
}

function renderProducts(products) {
    if (!productGrid) return;
    productGrid.innerHTML = '';

    if (!products || products.length === 0) {
        productGrid.innerHTML = '<div class="col-12 text-muted small">No products found.</div>';
        return;
    }

    products.forEach((product) => {
        const price = product.discountedPrice ?? product.mrp;
        const image = product.imageUrl && product.imageUrl.trim() !== ''
            ? product.imageUrl
            : 'https://dummyimage.com/600x400/f0f2f8/7d8ca6&text=RevShop';

        const col = document.createElement('div');
        col.className = 'col-md-6';
        col.innerHTML = `
            <div class="card product-card h-100">
                <img class="product-image" src="${image}" alt="Product image">
                <div class="card-body">
                    <h6 class="fw-bold"></h6>
                    <p class="small text-muted mb-1"></p>
                    <p class="small mb-2"></p>
                    <div class="d-flex align-items-center gap-2 mb-2">
                        <span class="fw-bold text-primary">INR ${price}</span>
                    </div>
                    <div class="d-flex gap-2">
                        <input class="form-control form-control-sm" id="qty_${product.id}" type="number" min="1" value="1">
                        <button class="btn btn-sm btn-primary" type="button">Add Cart</button>
                    </div>
                </div>
            </div>
        `;

        const title = col.querySelector('h6');
        const category = col.querySelector('.text-muted');
        const description = col.querySelectorAll('p')[1];
        const addBtn = col.querySelector('button');
        title.textContent = product.name || '';
        category.textContent = product.category?.name || '';
        description.textContent = product.description || '';
        addBtn.onclick = () => addToCart(product.id);
        productGrid.appendChild(col);
    });
}

async function loadCategories() {
    const res = await fetch('/api/categories');
    const categories = await res.json().catch(() => []);
    renderCategoryChips(categories);
}

async function loadProducts() {
    const keyword = (buyerSearch?.value || '').trim();
    let url = '/api/products?page=0&size=24';

    if (buyerCategory !== 'all') {
        url = `/api/products/category/${encodeURIComponent(buyerCategory)}?page=0&size=24`;
    } else if (keyword) {
        url = `/api/products/search?keyword=${encodeURIComponent(keyword)}&page=0&size=24`;
    }

    const res = await fetch(url);
    const body = await res.json().catch(() => ({ content: [] }));
    renderProducts(body.content || []);
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

document.addEventListener('DOMContentLoaded', async () => {
    await loadCategories();
    await loadProducts();
});
