async function registerUser(event, formId, endpoint, messageId) {
    event.preventDefault();

    const form = document.getElementById(formId);
    const payload = Object.fromEntries(new FormData(form).entries());

    const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content') || 'X-XSRF-TOKEN';

    const headers = { 'Content-Type': 'application/json' };
    if (csrfToken) {
        headers[csrfHeader] = csrfToken;
    }

    const msgEl = document.getElementById(messageId);
    let message = 'Request failed';
    let success = false;

    try {
        const res = await fetch(endpoint, {
            method: 'POST',
            headers,
            body: JSON.stringify(payload)
        });

        if (res.redirected && res.url.includes('/login')) {
            throw new Error('Session expired. Please refresh and try again.');
        }

        const contentType = res.headers.get('content-type') || '';
        if (contentType.includes('application/json')) {
            const body = await res.json();
            success = !!body.success && res.ok;
            message = body.message || message;

            if (body.details) {
                const details = Object.entries(body.details)
                    .map(([k, v]) => `${k}: ${v}`)
                    .join(', ');
                message = `${message}. ${details}`;
            }
        } else {
            const text = await res.text();
            message = text ? text.substring(0, 200) : 'Unexpected response from server';
        }
    } catch (err) {
        message = err.message || 'Unable to reach server';
    }

    msgEl.textContent = message;
    msgEl.className = success ? 'mt-3 small text-success' : 'mt-3 small text-danger';

    if (success) {
        setTimeout(() => window.location.href = '/login', 1200);
    }
}
