const buyerSearch = document.getElementById('buyerSearch');
let buyerCategory = 'all';

if (buyerSearch) {
    buyerSearch.addEventListener('input', applyBuyerFilters);
}

function buyerFilterCategory(category) {
    buyerCategory = (category || 'all').toLowerCase();
    applyBuyerFilters();
}

function applyBuyerFilters() {
    const search = (buyerSearch?.value || '').toLowerCase();
    document.querySelectorAll('#buyerProductGrid > div').forEach(card => {
        const name = (card.dataset.name || '').toLowerCase();
        const category = (card.dataset.category || '').toLowerCase();

        const matchesSearch = !search || name.includes(search);
        const matchesCategory = buyerCategory === 'all' || category === buyerCategory;
        card.style.display = matchesSearch && matchesCategory ? '' : 'none';
    });
}

async function markNotification(id) {
    const token = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content') || 'X-XSRF-TOKEN';

    const res = await fetch(`/api/notifications/${id}/read`, {
        method: 'PUT',
        headers: token ? { [header]: token } : {}
    });

    if (res.ok) {
        window.location.reload();
    }
}
