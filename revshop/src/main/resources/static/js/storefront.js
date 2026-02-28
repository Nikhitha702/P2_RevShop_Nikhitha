async function searchProducts() {
    const keyword = document.getElementById('searchKeyword').value.trim();
    if (!keyword) {
        await loadProducts();
        return;
    }
    const data = await revshop.callApi(`/api/products/search?keyword=${encodeURIComponent(keyword)}`);
    if (!data) return;
    renderProducts(data.content || []);
}

async function browseCategory(category) {
    const data = await revshop.callApi(`/api/products/category/${encodeURIComponent(category)}`);
    if (!data) return;
    renderProducts(data.content || []);
}

async function loadProducts() {
    const data = await revshop.callApi('/api/products');
    if (!data) return;
    renderProducts(data.content || []);
}

function renderProducts(products) {
    const root = document.getElementById('productGrid');
    root.innerHTML = '';
    if (!products.length) {
        root.innerHTML = '<div class="panel">No products found.</div>';
        return;
    }

    for (const product of products) {
        const card = document.createElement('article');
        card.className = 'card';
        const discount = product.discountedPrice || product.price;
        card.innerHTML = `
            <h3>${product.name}</h3>
            <p class="muted">${product.description || ''}</p>
            <p class="small">Category: ${product.category?.name || '-'}</p>
            <p class="small">Stock: ${product.quantity ?? 0}</p>
            <div class="price">${discount}</div>
            <div class="strike">MRP: ${product.price}</div>
            <div class="row" style="margin-top:0.6rem;">
                <button class="btn btn-primary" onclick="addToCart(${product.id})">Add to Cart</button>
                <button class="btn btn-secondary" onclick="addFavorite(${product.id})">Favorite</button>
                <button class="btn btn-secondary" onclick="loadReviews(${product.id})">Reviews</button>
            </div>
        `;
        root.appendChild(card);
    }
}

async function addToCart(productId) {
    const qty = prompt('Quantity?', '1') || '1';
    await revshop.callApi(`/api/cart/add?productId=${productId}&quantity=${qty}`, { method: 'POST' });
}

async function addFavorite(productId) {
    await revshop.callApi(`/api/favorites/${productId}`, { method: 'POST' });
}

async function loadReviews(productId) {
    await revshop.callApi(`/api/reviews/product/${productId}`);
}

window.addEventListener('DOMContentLoaded', async () => {
    revshop.setLog('outputLog');
});
