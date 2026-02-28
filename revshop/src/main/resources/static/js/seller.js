window.addEventListener('DOMContentLoaded', () => {
    revshop.setLog('sellerLog');
});

async function addProduct() {
    const payload = {
        name: document.getElementById('pName').value,
        description: document.getElementById('pDesc').value,
        price: Number(document.getElementById('pPrice').value),
        discountedPrice: Number(document.getElementById('pDiscount').value),
        quantity: Number(document.getElementById('pQty').value),
        inventoryThreshold: Number(document.getElementById('pThreshold').value),
        categoryName: document.getElementById('pCategory').value
    };
    await revshop.callApi('/api/products', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    });
}

async function updateProduct() {
    const id = document.getElementById('uId').value;
    const payload = {
        quantity: Number(document.getElementById('uQty').value),
        price: Number(document.getElementById('uPrice').value),
        discountedPrice: Number(document.getElementById('uDiscount').value)
    };
    await revshop.callApi(`/api/products/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    });
}

async function deleteProduct() {
    const id = document.getElementById('uId').value;
    await revshop.callApi(`/api/products/${id}`, { method: 'DELETE' });
}

async function loadInventory() {
    await revshop.callApi('/api/products/inventory');
}

async function loadLowStock() {
    await revshop.callApi('/api/products/inventory/low-stock');
}

async function loadSellerOrders() {
    await revshop.callApi('/api/orders/seller-orders');
}

async function updateOrderStatus() {
    const id = document.getElementById('orderId').value;
    const status = document.getElementById('status').value;
    await revshop.callApi(`/api/orders/update-status?orderId=${id}&status=${status}`, { method: 'PUT' });
}

async function listNotifications() {
    await revshop.callApi('/api/notifications');
}
