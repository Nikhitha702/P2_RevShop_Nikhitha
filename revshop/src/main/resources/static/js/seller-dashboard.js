function csrfHeaders() {
    const token = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content') || 'X-XSRF-TOKEN';
    return token ? { [header]: token, 'Content-Type': 'application/json' } : { 'Content-Type': 'application/json' };
}

function csrfOnlyHeaders() {
    const token = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content') || 'X-XSRF-TOKEN';
    return token ? { [header]: token } : {};
}

async function sellerApi(url, method = 'GET', body = null) {
    const headers = body === null ? csrfOnlyHeaders() : csrfHeaders();
    const options = { method, headers };
    if (body !== null) {
        options.body = JSON.stringify(body);
    }

    const res = await fetch(url, options);
    const contentType = res.headers.get('content-type') || '';
    const data = contentType.includes('application/json') ? await res.json() : await res.text();
    return { ok: res.ok, data };
}

async function loadCategories() {
    const select = document.getElementById('categorySelect');
    if (!select) return;

    const res = await fetch('/api/categories');
    const categories = await res.json().catch(() => []);

    select.innerHTML = '<option value="">Select category</option>';
    categories.forEach((c) => {
        const opt = document.createElement('option');
        opt.value = c.name;
        opt.textContent = c.name;
        select.appendChild(opt);
    });
}

async function addProduct(event) {
    event.preventDefault();
    const form = document.getElementById('addProductForm');
    const formData = new FormData(form);

    const res = await fetch('/api/products/upload', {
        method: 'POST',
        headers: csrfOnlyHeaders(),
        body: formData
    });

    const msgEl = document.getElementById('sellerMessage');
    const body = await res.json().catch(() => ({ message: 'Request failed' }));
    msgEl.textContent = body.message || 'Request completed';
    msgEl.className = res.ok ? 'small mt-2 text-success' : 'small mt-2 text-danger';

    if (res.ok) {
        form.reset();
        await loadCategories();
        setTimeout(() => window.location.reload(), 600);
    }
}

async function addCategory(event) {
    event.preventDefault();
    const form = document.getElementById('addCategoryForm');
    const payload = Object.fromEntries(new FormData(form).entries());

    const res = await fetch('/api/categories', {
        method: 'POST',
        headers: csrfHeaders(),
        body: JSON.stringify(payload)
    });

    const msgEl = document.getElementById('categoryMessage');
    const body = await res.json().catch(() => ({ message: 'Request failed' }));
    msgEl.textContent = body.message || 'Request completed';
    msgEl.className = res.ok ? 'small mt-2 text-success' : 'small mt-2 text-danger';

    if (res.ok) {
        form.reset();
        await loadCategories();
        setTimeout(() => window.location.reload(), 400);
    }
}

async function quickUpdateQty(id, currentQty) {
    const next = window.prompt('Enter new quantity', currentQty);
    if (next === null) {
        return;
    }

    const qty = Number(next);
    if (!Number.isInteger(qty) || qty < 0) {
        alert('Quantity must be a non-negative integer.');
        return;
    }

    const res = await fetch(`/api/products/${id}`, {
        method: 'PUT',
        headers: csrfHeaders(),
        body: JSON.stringify({ quantity: qty })
    });

    if (res.ok) {
        window.location.reload();
    }
}

async function deleteProduct(id) {
    const headers = csrfHeaders();
    delete headers['Content-Type'];

    const res = await fetch(`/api/products/${id}`, {
        method: 'DELETE',
        headers
    });

    if (res.ok) {
        window.location.reload();
    }
}

async function loadSellerReviews() {
    const target = document.getElementById('sellerReviews');
    if (!target) {
        return;
    }

    const res = await fetch('/api/reviews/seller');
    const reviews = await res.json().catch(() => []);

    if (!Array.isArray(reviews) || reviews.length === 0) {
        target.innerHTML = '<div class="text-muted small">No product reviews yet.</div>';
        return;
    }

    target.innerHTML = reviews.map((r) => `
        <div class="list-group-item px-0">
            <div class="fw-semibold">${r.product?.name || 'Product'} - ${r.rating}/5</div>
            <div class="small">${r.reviewText || ''}</div>
            <small class="text-muted">By ${r.buyer?.firstName || 'Buyer'} ${r.buyer?.lastName || ''}</small>
        </div>
    `).join('');
}

async function loadSellerOrders() {
    const target = document.getElementById('sellerOrders');
    const totalPaidEl = document.getElementById('sellerTotalPaid');
    const paidCountEl = document.getElementById('sellerPaidOrders');
    const unpaidCountEl = document.getElementById('sellerUnpaidOrders');
    if (!target) {
        return;
    }

    const res = await sellerApi('/api/payments/seller-overview');
    if (!res.ok || !res.data || !Array.isArray(res.data.orders)) {
        target.innerHTML = '<div class="text-danger small">Failed to load seller orders.</div>';
        return;
    }

    const summary = res.data;
    if (totalPaidEl) {
        totalPaidEl.textContent = `INR ${formatInr(summary.totalPaidAmount)}`;
    }
    if (paidCountEl) {
        paidCountEl.textContent = String(summary.paidOrdersCount ?? 0);
    }
    if (unpaidCountEl) {
        unpaidCountEl.textContent = String(summary.unpaidOrdersCount ?? 0);
    }

    if (summary.orders.length === 0) {
        target.innerHTML = '<div class="text-muted small">No orders for your products yet.</div>';
        return;
    }

    target.innerHTML = summary.orders.map((order) => {
        const paidBadge = order.paymentStatus === 'PAID'
            ? '<span class="badge text-bg-success ms-2">PAID</span>'
            : '<span class="badge text-bg-warning ms-2">UNPAID</span>';

        const paidAt = order.paidAt ? new Date(order.paidAt).toLocaleString() : 'Not paid yet';
        const method = order.paymentMethod || '-';

        return `
            <div class="border rounded p-2 mb-2">
                <div class="fw-semibold">Order #${order.orderId} - ${order.orderStatus} ${paidBadge}</div>
                <div class="small">Buyer: ${order.buyerName || 'Buyer'}</div>
                <div class="small">Your Amount: INR ${formatInr(order.sellerAmount)}</div>
                <div class="small">Payment Method: ${method}</div>
                <div class="small">Paid At: ${paidAt}</div>
            </div>
        `;
    }).join('');
}

function formatInr(value) {
    const num = Number(value ?? 0);
    if (Number.isNaN(num)) {
        return value ?? 0;
    }
    return num.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

async function updateOrderStatus() {
    const orderId = Number(document.getElementById('sellerOrderId')?.value);
    const status = document.getElementById('sellerOrderStatus')?.value;
    if (!Number.isInteger(orderId) || orderId <= 0 || !status) {
        return;
    }

    await sellerApi(`/api/orders/status?orderId=${orderId}&status=${status}`, 'PUT');
    await loadSellerOrders();
}

document.addEventListener('DOMContentLoaded', async () => {
    await loadCategories();
    await loadSellerReviews();
    await loadSellerOrders();
});
