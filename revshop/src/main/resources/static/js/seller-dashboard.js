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
        setTimeout(() => window.location.reload(), 600);
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
