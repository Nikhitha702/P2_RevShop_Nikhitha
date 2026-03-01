window.revshop = {
    logElementId: 'outputLog',
    setLog(targetId) {
        this.logElementId = targetId;
    },
    print(payload) {
        const el = document.getElementById(this.logElementId);
        if (!el) return;
        if (typeof payload === 'string') {
            el.textContent = payload;
            return;
        }
        el.textContent = JSON.stringify(payload, null, 2);
    },
    getCookie(name) {
        const value = `; ${document.cookie}`;
        const parts = value.split(`; ${name}=`);
        if (parts.length === 2) {
            return parts.pop().split(';').shift();
        }
        return null;
    },
    attachCsrf(options = {}) {
        const method = (options.method || 'GET').toUpperCase();
        if (["GET", "HEAD", "OPTIONS", "TRACE"].includes(method)) {
            return options;
        }

        const token = this.getCookie('XSRF-TOKEN');
        const headers = { ...(options.headers || {}) };
        if (token) {
            headers['X-XSRF-TOKEN'] = token;
        }

        return { ...options, headers };
    },
    async callApi(url, options) {
        const requestOptions = this.attachCsrf(options || {});
        const res = await fetch(url, requestOptions);
        const contentType = res.headers.get('content-type') || '';
        let payload;
        if (contentType.includes('application/json')) {
            payload = await res.json();
        } else {
            payload = await res.text();
        }
        if (!res.ok) {
            this.print({ status: res.status, error: payload });
            return null;
        }
        this.print(payload);
        return payload;
    }
};
