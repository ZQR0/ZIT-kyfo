const API = '/api/v1';

const SERVICE_POINTS = [
    { id: 1, name: 'Кафе «Небо»', active: true },
    { id: 2, name: 'Кофейня «Высота»', active: true },
    { id: 3, name: 'Бистро «Полёт»', active: true },
    { id: 4, name: 'Кафе «Терминал»', active: false },
    { id: 5, name: 'Кафе «Север»', active: true },
    { id: 6, name: 'Снек-бар «Пулково»', active: true },
    { id: 7, name: 'Кафе «Казань»', active: true },
    { id: 8, name: 'Кофейня «Урал»', active: true },
    { id: 9, name: 'Кафе «Черноморье»', active: true },
    { id: 10, name: 'Бистро «Восток»', active: true },
    { id: 11, name: 'Кафе «Дон»', active: true },
    { id: 12, name: 'Кофейня «Юг»', active: true },
    { id: 13, name: 'Бистро «Сибирь»', active: true },
    { id: 14, name: 'Кафе «Башкирия»', active: true },
    { id: 15, name: 'Снек-бар «Волга»', active: true },
    { id: 16, name: 'Кафе «Кавказ»', active: true },
    { id: 17, name: 'Кофейня «Самара»', active: true },
    { id: 18, name: 'Кафе «Транзит»', active: true },
    { id: 19, name: 'Бистро «Кубань»', active: false },
    { id: 20, name: 'Кафе «Ночной рейс»', active: true },
];

ACCRUAL_POINTS = [
    {id: 1, name: 'Системное начисление', active: true},
]

function fillServicePointSelect(selectId, activeOnly = true) {
    const select = document.getElementById(selectId);
    if (!select) return;

    const points = activeOnly ? SERVICE_POINTS.filter(p => p.active) : SERVICE_POINTS;
    select.innerHTML = points.map(p =>
        `<option value="${p.id}">${p.name} (${p.id})</option>`
    ).join('');
}

function fillServicePointAccrualSelect(selectId, activeOnly = true) {
    const select = document.getElementById(selectId);
    if (!select) return;

    const points = activeOnly ? ACCRUAL_POINTS.filter(p => p.active) : ACCRUAL_POINTS;
    select.innerHTML = points.map(p =>
    `<option value="${p.id}">${p.name}</option>`
    ).join('')
}

function formatShortDate(iso) {
    if (!iso) return '—';
    return new Date(iso).toLocaleString('ru-RU', {
        day: '2-digit', month: '2-digit',
        hour: '2-digit', minute: '2-digit',
    });
}

function formatFlightLabel(f) {
    const airline = f.airlineName.length > 14
        ? f.airlineName.slice(0, 13) + '…'
        : f.airlineName;
    const delay = f.delayed ? ` · +${f.delayMinutes}м` : '';
    return `${airline} · ${f.airportFrom}→${f.airportTo} · ${formatShortDate(f.timeOut)}${delay}`;
}

function fillFlightSelect(flights) {
    const select = document.getElementById('topup-flight-id');
    if (!select) return;

    const delayedFlights = flights.filter(f => f.delayed);

    if (!delayedFlights.length) {
        select.innerHTML = '<option value="">Нет задержанных рейсов (от 2 ч)</option>';
        select.disabled = true;
        return;
    }

    select.disabled = false;
    select.innerHTML = delayedFlights.map(f => {
        const title = `${f.airlineName}, ${f.airportFrom} → ${f.airportTo}, ${formatDate(f.timeOut)}`;
        return `<option value="${f.id}" title="${title}">${formatFlightLabel(f)}</option>`;
    }).join('');
}

function renderFlightsTable(flights) {
    const tbody = document.querySelector('#flights-table tbody');

    if (!flights.length) {
        tbody.innerHTML = '<tr><td colspan="6" class="empty-row">Рейсов не найдено</td></tr>';
        return;
    }

    tbody.innerHTML = flights.map(f => `
        <tr>
            <td>${f.id}</td>
            <td>${f.airlineName}</td>
            <td>${f.airportFrom} → ${f.airportTo}</td>
            <td>${formatDate(f.timeOut)}</td>
            <td>${formatDate(f.timeIn)}</td>
            <td class="${f.delayed ? 'delayed' : ''}">
                ${f.delayMinutes > 0 ? f.delayMinutes + ' мин' : '—'}
            </td>
        </tr>
    `).join('');
}

fillServicePointSelect('pay-point-id');
fillServicePointAccrualSelect('topup-point-id');

function showToast(message, isError = false) {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.className = 'toast' + (isError ? ' error' : '');
    toast.classList.remove('hidden');
    setTimeout(() => toast.classList.add('hidden'), 3500);
}

async function api(url, options = {}) {
    const response = await fetch(API + url, {
        credentials: 'include',
        headers: { 'Content-Type': 'application/json', ...options.headers },
        ...options,
    });

    if (!response.ok) {
        let message = `Ошибка ${response.status}`;
        try {
            const body = await response.json();
            message = body.error || body.message || message;
        } catch (_) { /* empty */ }
        throw new Error(message);
    }

    if (response.status === 204) return null;

    const text = await response.text();
    return text ? JSON.parse(text) : null;
}

function formatDate(iso) {
    if (!iso) return '—';
    return new Date(iso).toLocaleString('ru-RU', {
        day: '2-digit', month: '2-digit', year: 'numeric',
        hour: '2-digit', minute: '2-digit',
    });
}

function formatMoney(value) {
    return Number(value).toLocaleString('ru-RU', { minimumFractionDigits: 2 }) + ' ₽';
}

// --- Tabs ---

document.querySelectorAll('.tab').forEach(tab => {
    tab.addEventListener('click', () => {
        document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
        document.querySelectorAll('.panel').forEach(p => p.classList.remove('active'));
        tab.classList.add('active');
        document.getElementById(tab.dataset.tab).classList.add('active');
    });
});

// --- Auth ---

const loginBlock = document.getElementById('login-block');
const cabinetBlock = document.getElementById('cabinet-block');

function showCabinet(airline) {
    loginBlock.classList.add('hidden');
    cabinetBlock.classList.remove('hidden');
    document.getElementById('airline-name').textContent = airline.name;
    loadFlights();
    loadTickets();
}

function showLogin() {
    loginBlock.classList.remove('hidden');
    cabinetBlock.classList.add('hidden');
}

document.getElementById('login-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
        const airline = await api('/airlines/login', {
            method: 'POST',
            body: JSON.stringify({
                login: document.getElementById('login').value,
                password: document.getElementById('password').value,
            }),
        });
        showCabinet(airline);
        showToast(`Добро пожаловать, ${airline.name}`);
    } catch (err) {
        showToast(err.message, true);
    }
});

document.getElementById('logout-btn').addEventListener('click', async () => {
    try {
        await api('/airlines/logout', { method: 'POST' });
        showLogin();
        showToast('Вы вышли из системы');
    } catch (err) {
        showToast(err.message, true);
    }
});

// --- Flights ---

async function loadFlights() {
    try {
        const delayedOnly = document.getElementById('filter-delayed').checked;
        const url = delayedOnly ? '/airlines/flights?delayedOnly=true' : '/airlines/flights';
        const flights = await api(url);
        fillFlightSelect(flights);
        renderFlightsTable(flights);
    } catch (err) {
        showToast(err.message, true);
    }
}

document.getElementById('load-flights').addEventListener('click', loadFlights);
document.getElementById('filter-delayed').addEventListener('change', loadFlights);

// --- Tickets ---

async function loadTickets() {
    try {
        const tickets = await api('/airlines/tickets');
        const tbody = document.querySelector('#tickets-table tbody');
        tbody.innerHTML = tickets.map(t => `
            <tr>
                <td>${t.ticketNumber}</td>
                <td>${t.flightId}</td>
                <td>${t.seat || '—'}</td>
                <td>${formatMoney(t.balance)}</td>
            </tr>
        `).join('');
    } catch (err) {
        showToast(err.message, true);
    }
}

document.getElementById('load-tickets').addEventListener('click', loadTickets);


document.getElementById('topup-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const flightId = document.getElementById('topup-flight-id').value;
    try {
        const result = await api(`/airlines/flights/${flightId}/payment/processTopUp`, {
            method: 'POST',
            body: JSON.stringify({
                amount: document.getElementById('topup-amount').value,
                servicePointId: Number(document.getElementById('topup-point-id').value),
            }),
        });
        showToast(result.message || 'Начисление выполнено');
        loadTickets();
        loadFlights();
    } catch (err) {
        showToast(err.message, true);
    }
});

// --- Cafe terminal ---

document.getElementById('balance-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const el = document.getElementById('balance-result');
    const ticket = document.getElementById('balance-ticket').value;
    try {
        const balance = await api(`/points/checkBalance?ticketNumber=${encodeURIComponent(ticket)}`);
        el.textContent = `Баланс: ${formatMoney(balance)}`;
        el.className = 'result';
    } catch (err) {
        el.textContent = err.message;
        el.className = 'result error';
    }
});

document.getElementById('pay-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const el = document.getElementById('pay-result');
    try {
        const result = await api('/points/pay', {
            method: 'POST',
            body: JSON.stringify({
                ticketNumber: document.getElementById('pay-ticket').value,
                amount: document.getElementById('pay-amount').value,
                servicePointId: Number(document.getElementById('pay-point-id').value),
            }),
        });
        el.textContent = result.message + (result.newBalance != null ? ` Новый баланс: ${formatMoney(result.newBalance)}` : '');
        el.className = 'result';
    } catch (err) {
        el.textContent = err.message;
        el.className = 'result error';
    }
});
