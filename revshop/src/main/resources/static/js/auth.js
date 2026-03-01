async function register(event, formId, endpoint, messageId, redirectToLogin = true) {
    event.preventDefault();
    const form = document.getElementById(formId);
    const payload = Object.fromEntries(new FormData(form).entries());

    const csrfMeta = document.querySelector('meta[name="_csrf"]');
    const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');
    const tokenFromMeta = csrfMeta ? csrfMeta.getAttribute('content') : null;
    const headerName = csrfHeaderMeta ? csrfHeaderMeta.getAttribute('content') : 'X-XSRF-TOKEN';
    const tokenFromCookie = document.cookie
        .split('; ')
        .find(row => row.startsWith('XSRF-TOKEN='))
        ?.split('=')[1];
    const token = tokenFromMeta || (tokenFromCookie ? decodeURIComponent(tokenFromCookie) : null);

    const headers = { 'Content-Type': 'application/json' };
    if (token) {
        headers[headerName] = token;
    }

    const res = await fetch(endpoint, {
        method: 'POST',
        headers,
        body: JSON.stringify(payload)
    });

    const contentType = res.headers.get('content-type') || '';
    let data = null;
    let message = 'Request failed';

    if (contentType.includes('application/json')) {
        try {
            data = await res.json();
            message = data.message || data.error || message;
            if (data.details && typeof data.details === 'object') {
                const detailText = Object.entries(data.details)
                    .map(([key, value]) => `${key}: ${value}`)
                    .join(', ');
                message = `${message}. ${detailText}`;
            }
        } catch (e) {
            message = 'Unable to parse server response';
        }
    } else {
        const text = await res.text();
        if (text && text.trim().length > 0) {
            message = text.length > 180 ? `${text.substring(0, 180)}...` : text;
        }
    }

    const msgEl = document.getElementById(messageId);
    const success = !!(data && data.success && res.ok);
    msgEl.textContent = success ? (data.message || 'Registration successful') : message;
    msgEl.style.color = success ? '#16a34a' : '#dc2626';

    if (success && redirectToLogin) {
        setTimeout(() => window.location.href = '/login', 1200);
    }
}
