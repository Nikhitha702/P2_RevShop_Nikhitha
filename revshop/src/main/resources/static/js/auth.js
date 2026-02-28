async function register(event, formId, endpoint, messageId, redirectToLogin = true) {
    event.preventDefault();
    const form = document.getElementById(formId);
    const payload = Object.fromEntries(new FormData(form).entries());

    const res = await fetch(endpoint, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    });

    let data;
    try {
        data = await res.json();
    } catch (e) {
        data = { success: false, message: 'Unexpected response' };
    }

    const msgEl = document.getElementById(messageId);
    msgEl.textContent = data.message || 'Request completed';
    msgEl.style.color = data.success ? '#16a34a' : '#dc2626';

    if (data.success && redirectToLogin) {
        setTimeout(() => window.location.href = '/login', 1200);
    }
}
