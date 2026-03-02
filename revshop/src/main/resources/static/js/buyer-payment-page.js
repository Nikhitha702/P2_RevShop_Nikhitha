const payOrderIdEl = document.getElementById('payOrderId');
const payMethodEl = document.getElementById('payMethod');
const orderListEl = document.getElementById('orderList');

function setOrderForPayment(orderId) {
    if (payOrderIdEl) {
        payOrderIdEl.value = orderId;
    }
}

async function myOrders() {
    const res = await apiCall('/api/orders/my-orders');
    if (!res.ok || !Array.isArray(res.data) || !orderListEl) {
        return;
    }

    if (res.data.length === 0) {
        orderListEl.innerHTML = '<div class="text-muted small">No orders found.</div>';
        return;
    }

    orderListEl.innerHTML = res.data.map((order) => {
        const items = (order.items || []).map((i) => `${i.product?.name || 'Product'} x ${i.quantity}`).join(', ');
        return `
            <div class="panel-card mb-2 d-flex justify-content-between align-items-start gap-2">
                <div>
                    <div class="fw-semibold"><i class="bi bi-box-seam me-1"></i>Order #${order.id}</div>
                    <div class="small"><i class="bi bi-flag me-1"></i>${order.status}</div>
                    <div class="small"><i class="bi bi-cash-stack me-1"></i>INR ${order.totalAmount}</div>
                    <div class="small"><i class="bi bi-list-check me-1"></i>${items || 'N/A'}</div>
                </div>
                <button class="btn btn-sm btn-outline-success" onclick="setOrderForPayment(${order.id})" title="Use this order">
                    <i class="bi bi-check2-circle"></i>
                </button>
            </div>
        `;
    }).join('');
}

async function payOrder() {
    const orderId = Number(payOrderIdEl?.value);
    const method = payMethodEl?.value;
    if (!Number.isInteger(orderId) || orderId <= 0) {
        showToast('Enter a valid order ID', 'error');
        return;
    }
    if (!method) {
        showToast('Select payment method', 'error');
        return;
    }

    const res = await apiCall(`/api/payments/pay?orderId=${orderId}&method=${method}`, 'POST');
    if (res.ok) {
        await myOrders();
    }
}

document.addEventListener('DOMContentLoaded', async () => {
    const params = new URLSearchParams(window.location.search);
    const orderId = params.get('orderId');
    if (orderId && payOrderIdEl) {
        payOrderIdEl.value = orderId;
    }
    await myOrders();
});
