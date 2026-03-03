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
        msgEl.textContent = `${message} Redirecting to login...`;
        setTimeout(() => window.location.assign('/login'), 700);
    }
}

function buildHeaders() {
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content') || 'X-XSRF-TOKEN';
    const headers = { 'Content-Type': 'application/json' };
    if (csrfToken) {
        headers[csrfHeader] = csrfToken;
    }
    return headers;
}

async function postJson(endpoint, payload) {
    const response = await fetch(endpoint, {
        method: 'POST',
        headers: buildHeaders(),
        body: JSON.stringify(payload)
    });

    const contentType = response.headers.get('content-type') || '';
    const body = contentType.includes('application/json') ? await response.json() : { message: 'Unexpected response from server' };
    return { response, body };
}

async function requestForgotPassword(event) {
    event.preventDefault();
    const form = document.getElementById('forgotPasswordForm');
    const msgEl = document.getElementById('forgotPasswordMessage');
    const payload = Object.fromEntries(new FormData(form).entries());

    try {
        const { response, body } = await postJson('/api/auth/forgot-password', payload);
        msgEl.textContent = body.message || 'Request failed';
        msgEl.className = response.ok ? 'mt-2 small text-success' : 'mt-2 small text-danger';
    } catch (err) {
        msgEl.textContent = err.message || 'Unable to reach server';
        msgEl.className = 'mt-2 small text-danger';
    }
}

async function submitResetPassword(event) {
    event.preventDefault();
    const form = document.getElementById('resetPasswordForm');
    const msgEl = document.getElementById('resetPasswordMessage');
    const payload = Object.fromEntries(new FormData(form).entries());

    try {
        const { response, body } = await postJson('/api/auth/reset-password', payload);
        msgEl.textContent = body.message || 'Request failed';
        msgEl.className = response.ok ? 'mt-2 small text-success' : 'mt-2 small text-danger';
        if (response.ok) {
            setTimeout(() => window.location.assign('/login'), 800);
        }
    } catch (err) {
        msgEl.textContent = err.message || 'Unable to reach server';
        msgEl.className = 'mt-2 small text-danger';
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const token = new URLSearchParams(window.location.search).get('resetToken');
    const tokenInput = document.getElementById('resetTokenInput');
    if (token && tokenInput) {
        tokenInput.value = token;
    }
});
