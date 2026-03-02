const shipAddressEl = document.getElementById('shipAddress');
const billAddressEl = document.getElementById('billAddress');

async function checkoutOrder() {
    const shippingAddress = shipAddressEl?.value?.trim() || '';
    const billingAddress = billAddressEl?.value?.trim() || '';

    if (shippingAddress.length < 5 || billingAddress.length < 5) {
        showToast('Enter valid shipping and billing addresses', 'error');
        return;
    }

    const res = await apiCall('/api/orders/checkout', 'POST', { shippingAddress, billingAddress });
    if (!res.ok || !res.data || typeof res.data !== 'object') {
        return;
    }

    const orderId = res.data.id;
    if (orderId) {
        showToast(`Order #${orderId} created. Continue to payment.`, 'success');
        setTimeout(() => {
            window.location.assign(`/buyer/payment?orderId=${orderId}`);
        }, 700);
    }
}
