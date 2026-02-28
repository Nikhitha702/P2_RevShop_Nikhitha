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
    async callApi(url, options) {
        const res = await fetch(url, options || {});
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
