const buyerSearch = document.getElementById('buyerSearch');
let buyerCategory = 'all';
const toastContainer = document.getElementById('toastContainer');
const categoryWrap = document.getElementById('buyerCategories');
const productGrid = document.getElementById('buyerProductGrid');
const cartListEl = document.getElementById('cartList');
const orderListEl = document.getElementById('orderList');
const favoriteListEl = document.getElementById('favoriteList');
const reviewSelectedNameEl = document.getElementById('reviewSelectedName');
const reviewSummaryTextEl = document.getElementById('reviewSummaryText');
const reviewListEl = document.getElementById('reviewList');
let buyerAllProducts = [];

if (buyerSearch) {
    buyerSearch.addEventListener('input', loadProducts);
}

function showToast(message, type = 'info') {
    if (!toastContainer || !message) {
        return;
    }

    const toast = document.createElement('div');
    toast.className = `toast-msg ${type}`;
    toast.textContent = message;
    toastContainer.appendChild(toast);

    setTimeout(() => {
        toast.classList.add('fade-out');
        setTimeout(() => toast.remove(), 250);
    }, 2600);
}

function payloadMessage(data, ok) {
    if (data && typeof data === 'object' && !Array.isArray(data)) {
        if (typeof data.message === 'string' && data.message.trim() !== '') {
            return data.message;
        }
        if (typeof data.success === 'boolean') {
            return data.success ? 'Action completed successfully' : 'Action failed';
        }
    }
    if (Array.isArray(data)) {
        return `Loaded ${data.length} item(s)`;
    }
    if (typeof data === 'string' && data.trim() !== '') {
        return data;
    }
    return ok ? 'Action completed successfully' : 'Request failed';
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
    const msg = payloadMessage(data, res.ok);
    showToast(msg, res.ok ? 'success' : 'error');
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
            : '/images/product-placeholder.svg';

        const col = document.createElement('div');
        col.className = 'col-md-6';
        col.innerHTML = `
            <div class="card product-card h-100">
                <img class="product-image" src="${image}" onerror="this.onerror=null;this.src='/images/product-placeholder.svg';" alt="Product image">
                <div class="card-body">
                    <h6 class="fw-bold"></h6>
                    <p class="small text-muted mb-1"></p>
                    <p class="small mb-2"></p>
                    <div class="d-flex align-items-center gap-2 mb-2">
                        <span class="fw-bold text-primary">INR ${price}</span>
                    </div>
                    <div class="small text-muted mb-2">Available: ${product.quantity ?? 0}</div>
                    <div class="d-flex gap-2">
                        <input class="form-control form-control-sm" id="qty_${product.id}" type="number" min="1" value="1">
                        <button class="btn btn-sm btn-primary icon-only-btn" type="button" title="Add to Cart"><i class="bi bi-cart-plus"></i></button>
                    </div>
                    <div class="d-flex gap-2 mt-2">
                        <button class="btn btn-sm btn-outline-danger" type="button" title="Add to Favorites"><i class="bi bi-heart-fill me-1"></i>Favorite</button>
                        <button class="btn btn-sm btn-outline-info" type="button" title="Review this product"><i class="bi bi-chat-square-dots me-1"></i>Review</button>
                    </div>
                </div>
            </div>
        `;

        const title = col.querySelector('h6');
        const category = col.querySelector('.text-muted');
        const description = col.querySelectorAll('p')[1];
        const [addBtn, favBtn, reviewsBtn] = col.querySelectorAll('button');
        title.textContent = product.name || '';
        category.textContent = product.category?.name || '';
        description.textContent = product.description || '';
        addBtn.onclick = () => addToCart(product.id);
        favBtn.onclick = () => addToFavorites(product.id);
        reviewsBtn.onclick = () => viewReviews(product.id, product.name);
        productGrid.appendChild(col);
    });
}

async function loadCategories() {
    const res = await fetch('/api/categories');
    const categories = await res.json().catch(() => []);
    renderCategoryChips(categories);
}

async function loadProducts() {
    if (buyerAllProducts.length === 0) {
        const res = await fetch('/api/products/all');
        buyerAllProducts = await res.json().catch(() => []);
    }

    const keyword = (buyerSearch?.value || '').trim().toLowerCase();
    const filtered = buyerAllProducts.filter((p) => {
        const matchesCategory = buyerCategory === 'all' || (p.category?.name || '').toLowerCase() === buyerCategory;
        const matchesSearch = !keyword || (p.name || '').toLowerCase().includes(keyword);
        return matchesCategory && matchesSearch;
    });

    renderProducts(filtered);
}

async function addToCart(productId) {
    const qtyInput = document.getElementById(`qty_${productId}`);
    const qty = qtyInput ? Number(qtyInput.value) : 1;
    await apiCall(`/api/cart/add?productId=${productId}&quantity=${qty}`, 'POST');
}

async function viewCart() {
    if (!cartListEl) {
        return;
    }
    const res = await apiCall('/api/cart');
    if (!res.ok || !Array.isArray(res.data)) {
        return;
    }

    if (res.data.length === 0) {
        cartListEl.innerHTML = '<div class="text-muted small">Your cart is empty.</div>';
        return;
    }

    cartListEl.innerHTML = res.data.map((item) => `
        <div class="panel-card mb-2">
            <div class="fw-semibold"><i class="bi bi-bag me-1"></i>${item.product?.name || 'Product'}</div>
            <div class="small"><i class="bi bi-cash-coin me-1"></i>INR ${item.product?.discountedPrice ?? item.product?.mrp ?? 0}</div>
            <div class="small mb-2"><i class="bi bi-123 me-1"></i>${item.quantity}</div>
            <div class="d-flex gap-2">
                <input id="cart_qty_${item.id}" type="number" min="1" value="${item.quantity}" class="form-control form-control-sm" style="max-width:140px;">
                <button class="btn btn-sm btn-outline-primary" onclick="updateCartItem(${item.id})" title="Update"><i class="bi bi-arrow-repeat"></i></button>
                <button class="btn btn-sm btn-outline-danger" onclick="removeCartItem(${item.id})" title="Remove"><i class="bi bi-trash3"></i></button>
            </div>
        </div>
    `).join('');
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
    if (!orderListEl) {
        return;
    }
    const res = await apiCall('/api/orders/my-orders');
    if (!res.ok || !Array.isArray(res.data)) {
        return;
    }

    if (res.data.length === 0) {
        orderListEl.innerHTML = '<div class="text-muted small">No orders found.</div>';
        return;
    }

    orderListEl.innerHTML = res.data.map((order) => {
        const items = (order.items || []).map((i) => `${i.product?.name || 'Product'} x ${i.quantity}`).join(', ');
        return `
            <div class="panel-card mb-2">
                <div class="fw-semibold"><i class="bi bi-box-seam me-1"></i>Order #${order.id}</div>
                <div class="small"><i class="bi bi-flag me-1"></i>${order.status}</div>
                <div class="small"><i class="bi bi-cash-stack me-1"></i>INR ${order.totalAmount}</div>
                <div class="small"><i class="bi bi-list-check me-1"></i>${items || 'N/A'}</div>
            </div>
        `;
    }).join('');
}

async function addToFavorites(productId) {
    const res = await apiCall(`/api/favorites/${productId}`, 'POST');
    if (res.ok) {
        await viewFavorites();
    }
}

async function viewFavorites() {
    if (!favoriteListEl) {
        return;
    }
    const res = await apiCall('/api/favorites');
    if (!res.ok || !Array.isArray(res.data)) {
        return;
    }

    if (res.data.length === 0) {
        favoriteListEl.innerHTML = '<div class="text-muted small">No favorite products yet.</div>';
        return;
    }

    favoriteListEl.innerHTML = res.data.map((f) => `
        <div class="panel-card mb-2 d-flex justify-content-between align-items-center">
            <div>
                <div class="fw-semibold"><i class="bi bi-heart-fill text-danger me-1"></i>${f.product?.name || 'Product'}</div>
                <div class="small"><i class="bi bi-upc-scan me-1"></i>${f.product?.id || '-'}</div>
            </div>
            <button class="btn btn-sm btn-outline-danger" onclick="removeFavoriteById(${f.product?.id || 0})" title="Remove Favorite"><i class="bi bi-heartbreak"></i></button>
        </div>
    `).join('');
}

async function removeFavorite() {
    const productId = Number(document.getElementById('favoriteProductId').value);
    if (!Number.isInteger(productId) || productId <= 0) {
        showToast('Enter a valid product ID for favorite removal', 'error');
        return;
    }
    await apiCall(`/api/favorites/${productId}`, 'DELETE');
    await viewFavorites();
}

async function removeFavoriteById(productId) {
    if (!Number.isInteger(productId) || productId <= 0) {
        return;
    }
    await apiCall(`/api/favorites/${productId}`, 'DELETE');
    await viewFavorites();
}

async function viewReviews(productId, productName = null) {
    const reviewProductEl = document.getElementById('reviewProductId');
    if (reviewProductEl) {
        reviewProductEl.value = productId;
    }
    if (reviewSelectedNameEl) {
        reviewSelectedNameEl.textContent = productName || `Product #${productId}`;
    }

    const res = await apiCall(`/api/reviews/product/${productId}/summary`);
    if (!res.ok || !res.data || typeof res.data !== 'object') {
        if (reviewSummaryTextEl) {
            reviewSummaryTextEl.textContent = 'Could not load review summary for this product.';
        }
        if (reviewListEl) {
            reviewListEl.innerHTML = '';
        }
        return;
    }

    const avg = res.data.averageRating ?? 0;
    const count = res.data.totalReviews ?? 0;
    if (reviewSummaryTextEl) {
        reviewSummaryTextEl.textContent = `Rating: ${avg}/5 from ${count} review(s).`;
    }
    if (reviewListEl) {
        const reviews = Array.isArray(res.data.reviews) ? res.data.reviews : [];
        if (reviews.length === 0) {
            reviewListEl.innerHTML = '<div class="small text-muted">No reviews yet for this product.</div>';
        } else {
            reviewListEl.innerHTML = reviews.slice(0, 5).map((r) => `
                <div class="panel-card mb-2">
                    <div class="fw-semibold"><i class="bi bi-star-fill text-warning me-1"></i>${r.rating}/5</div>
                    <div class="small">${r.reviewText || ''}</div>
                    <div class="small text-muted">By ${r.buyer?.firstName || 'Buyer'} ${r.buyer?.lastName || ''}</div>
                </div>
            `).join('');
        }
    }
    showToast(`Product ${productId}: ${avg}/5 from ${count} review(s)`, 'info');
}

async function submitReview() {
    const productId = Number(document.getElementById('reviewProductId').value);
    const rating = Number(document.getElementById('reviewRating').value);
    const reviewText = document.getElementById('reviewText').value;

    if (!Number.isInteger(productId) || productId <= 0) {
        showToast('Enter a valid product ID before submitting review', 'error');
        return;
    }

    const res = await apiCall(`/api/reviews/product/${productId}`, 'POST', { rating, reviewText });
    if (res.ok) {
        await viewReviews(productId, reviewSelectedNameEl ? reviewSelectedNameEl.textContent : null);
    }
}

async function payOrder() {
    const orderId = document.getElementById('payOrderId').value;
    const method = document.getElementById('payMethod').value;
    await apiCall(`/api/payments/pay?orderId=${orderId}&method=${method}`, 'POST');
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
    buyerAllProducts = [];
    await loadProducts();
    if (cartListEl) {
        await viewCart();
    }
    if (orderListEl) {
        await myOrders();
    }
    if (favoriteListEl) {
        await viewFavorites();
    }
});
