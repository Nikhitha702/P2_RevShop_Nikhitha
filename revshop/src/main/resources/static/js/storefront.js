const searchInput = document.getElementById('searchInput');
if (searchInput) {
    searchInput.addEventListener('input', applyFilters);
}

let selectedCategory = 'all';

function filterByCategory(category) {
    selectedCategory = (category || 'all').toLowerCase();
    applyFilters();
}

function applyFilters() {
    const search = (searchInput?.value || '').toLowerCase();
    document.querySelectorAll('#productGrid > div').forEach(card => {
        const name = (card.dataset.name || '').toLowerCase();
        const category = (card.dataset.category || '').toLowerCase();

        const matchesSearch = !search || name.includes(search);
        const matchesCategory = selectedCategory === 'all' || category === selectedCategory;
        card.style.display = matchesSearch && matchesCategory ? '' : 'none';
    });
}
