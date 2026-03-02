const toastContainer = document.getElementById('toastContainer');

function showToast(message, type = 'info') {
    if (!toastContainer || !message) {
        return;
    }

    const toast = document.createElement('div');
    toast.className = `toast-msg ${type}`;
    toast.textContent = message;
    toastContainer.appendChild(toast);

    setTimeout(() => {
        toast.classList.add('fade-out');
        setTimeout(() => toast.remove(), 250);
    }, 2600);
}

function payloadMessage(data, ok) {
    if (data && typeof data === 'object' && !Array.isArray(data)) {
        if (typeof data.message === 'string' && data.message.trim() !== '') {
            return data.message;
        }
        if (typeof data.success === 'boolean') {
            return data.success ? 'Action completed successfully' : 'Action failed';
        }
    }
    if (Array.isArray(data)) {
        return `Loaded ${data.length} item(s)`;
    }
    if (typeof data === 'string' && data.trim() !== '') {
        return data;
    }
    return ok ? 'Action completed successfully' : 'Request failed';
}

function csrfHeaders(includeJson = true) {
    const token = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content') || 'X-XSRF-TOKEN';
    const headers = includeJson ? { 'Content-Type': 'application/json' } : {};
    if (token) {
        headers[header] = token;
    }
    return headers;
}

async function apiCall(url, method = 'GET', body = null) {
    const opts = { method, headers: csrfHeaders(body !== null) };
    if (body !== null) {
        opts.body = JSON.stringify(body);
    }

    const res = await fetch(url, opts);
    const contentType = res.headers.get('content-type') || '';
    const data = contentType.includes('application/json') ? await res.json() : await res.text();
    const msg = payloadMessage(data, res.ok);
    showToast(msg, res.ok ? 'success' : 'error');
    return { ok: res.ok, data };
}
