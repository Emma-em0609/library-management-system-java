const API_URL = 'http://localhost:3000/api';



function getToken() {
    return localStorage.getItem('jwt_token');
}

function getRole() {
    return localStorage.getItem('jwt_role');
}

function getUsername() {
    return localStorage.getItem('jwt_username');
}

function isAdmin() {
    return getRole() === 'ROLE_ADMIN';
}


async function authFetch(url, options = {}) {
    const token = getToken();

    const headers = {
        'Content-Type': 'application/json',
        ...(options.headers || {}),
        ...(token ? { 'Authorization': `Bearer ${token}` } : {})
    };

    const response = await fetch(url, { ...options, headers });

    if (response.status === 401) {

        doLogout();
        return null;
    }

    if (response.status === 403) {
        alert('Недостатньо прав. Для цієї дії потрібна роль ADMIN.');
        return null;
    }

    return response;
}




function switchAuthTab(tab) {
    const isLogin = (tab === 'login');
    document.getElementById('login-panel').style.display = isLogin ? 'block' : 'none';
    document.getElementById('register-panel').style.display = isLogin ? 'none' : 'block';
    document.getElementById('tab-login-btn').classList.toggle('active', isLogin);
    document.getElementById('tab-register-btn').classList.toggle('active', !isLogin);
    // Очищаємо повідомлення при переключенні
    hideAuthMessages();
}

function hideAuthMessages() {
    ['login-error', 'register-error', 'register-success'].forEach(id => {
        const el = document.getElementById(id);
        if (el) { el.style.display = 'none'; el.textContent = ''; }
    });
}



async function doLogin() {
    const username = document.getElementById('login-username').value.trim();
    const password = document.getElementById('login-password').value.trim();
    const errorEl = document.getElementById('login-error');
    errorEl.style.display = 'none';

    if (!username || !password) {
        errorEl.textContent = 'Заповніть логін та пароль';
        errorEl.style.display = 'block';
        return;
    }

    try {
        const res = await fetch(`${API_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        const data = await res.json();

        if (!res.ok) {
            errorEl.textContent = data.error || 'Невірний логін або пароль';
            errorEl.style.display = 'block';
            return;
        }


        localStorage.setItem('jwt_token', data.token);
        localStorage.setItem('jwt_role', data.role);
        localStorage.setItem('jwt_username', data.username || username);

        showApp();

    } catch (e) {
        errorEl.textContent = 'Помилка підключення до сервера';
        errorEl.style.display = 'block';
        console.error('Login error:', e);
    }
}



async function doRegister() {
    const username = document.getElementById('reg-username').value.trim();
    const password = document.getElementById('reg-password').value.trim();
    const password2 = document.getElementById('reg-password2').value.trim();
    const errorEl = document.getElementById('register-error');
    const successEl = document.getElementById('register-success');
    errorEl.style.display = 'none';
    successEl.style.display = 'none';


    if (!username || !password || !password2) {
        errorEl.textContent = 'Заповніть всі поля';
        errorEl.style.display = 'block';
        return;
    }
    if (username.length < 3) {
        errorEl.textContent = 'Логін має бути не менше 3 символів';
        errorEl.style.display = 'block';
        return;
    }
    if (password.length < 4) {
        errorEl.textContent = 'Пароль має бути не менше 4 символів';
        errorEl.style.display = 'block';
        return;
    }
    if (password !== password2) {
        errorEl.textContent = 'Паролі не збігаються';
        errorEl.style.display = 'block';
        return;
    }

    try {
        const res = await fetch(`${API_URL}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        const data = await res.json();

        if (!res.ok) {
            errorEl.textContent = data.error || 'Помилка реєстрації';
            errorEl.style.display = 'block';
            return;
        }


        successEl.textContent = '✅ ' + data.message;
        successEl.style.display = 'block';
        document.getElementById('reg-username').value = '';
        document.getElementById('reg-password').value = '';
        document.getElementById('reg-password2').value = '';

        setTimeout(() => {
            switchAuthTab('login');
            document.getElementById('login-username').value = username;
        }, 1500);

    } catch (e) {
        errorEl.textContent = 'Помилка підключення до сервера';
        errorEl.style.display = 'block';
        console.error('Register error:', e);
    }
}



function doLogout() {
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('jwt_role');
    localStorage.removeItem('jwt_username');

    document.getElementById('app-screen').style.display = 'none';
    document.getElementById('auth-screen').style.display = 'flex';
    document.getElementById('login-username').value = '';
    document.getElementById('login-password').value = '';
    hideAuthMessages();
    switchAuthTab('login');
}





function showApp() {
    const role = getRole();
    const roleLabel = role === 'ROLE_ADMIN' ? 'ADMIN' : 'USER';
    const badgeColor = role === 'ROLE_ADMIN' ? 'badge-admin' : 'badge-user';

    const badge = document.getElementById('user-badge');
    badge.textContent = `${getUsername()} (${roleLabel})`;
    badge.className = `user-badge ${badgeColor}`;

    document.getElementById('auth-screen').style.display = 'none';
    document.getElementById('app-screen').style.display = 'block';

    applyRoleVisibility();
    loadBooks();
}


function applyRoleVisibility() {
    const admin = isAdmin();

    // Форми (додати / редагувати)
    document.getElementById('book-form-wrapper').style.display   = admin ? 'block' : 'none';
    document.getElementById('reader-form-wrapper').style.display = admin ? 'block' : 'none';
    document.getElementById('loan-form-wrapper').style.display   = admin ? 'block' : 'none';


    document.querySelectorAll('.col-actions').forEach(el => {
        el.style.display = admin ? '' : 'none';
    });
}


window.addEventListener('DOMContentLoaded', () => {
    if (getToken()) {
        showApp();
    } else {
        document.getElementById('auth-screen').style.display = 'flex';
        document.getElementById('app-screen').style.display = 'none';
    }
});




function showTab(tabName, event) {
    document.querySelectorAll('.tab-content').forEach(tab => tab.classList.remove('active'));
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));

    document.getElementById(`${tabName}-tab`).classList.add('active');
    event.target.classList.add('active');

    if (tabName === 'books')   loadBooks();
    if (tabName === 'readers') loadReaders();
    if (tabName === 'loans')   loadLoans();
}




async function loadBooks() {
    try {
        const response = await authFetch(`${API_URL}/books`);
        if (!response) return;

        const books = await response.json();
        const tbody = document.querySelector('#books-table tbody');
        tbody.innerHTML = '';

        books.forEach(book => {

            const actionsCell = isAdmin()
                ? `<td class="col-actions">
                       <button class="btn-edit" onclick="editBook(${book.id})">Редагувати</button>
                       <button class="btn-delete" onclick="deleteBook(${book.id})">Видалити</button>
                   </td>`
                : '';

            tbody.innerHTML += `
                <tr>
                    <td>${book.id}</td>
                    <td>${book.title}</td>
                    <td>${book.author}</td>
                    <td>${book.isbn || '-'}</td>
                    <td>${book.year || '-'}</td>
                    <td>${book.quantity}</td>
                    <td>${book.available}</td>
                    ${actionsCell}
                </tr>`;
        });
    } catch (error) {
        console.error('Помилка завантаження книг:', error);
    }
}

document.getElementById('book-form').addEventListener('submit', async (e) => {
    e.preventDefault();

    const id = document.getElementById('book-id').value;
    const book = {
        title:     document.getElementById('book-title').value,
        author:    document.getElementById('book-author').value,
        isbn:      document.getElementById('book-isbn').value,
        year:      parseInt(document.getElementById('book-year').value) || null,
        quantity:  parseInt(document.getElementById('book-quantity').value),
        available: parseInt(document.getElementById('book-quantity').value)
    };

    try {
        if (id) {
            await authFetch(`${API_URL}/books/${id}`, {
                method: 'PUT',
                body: JSON.stringify(book)
            });
        } else {
            await authFetch(`${API_URL}/books`, {
                method: 'POST',
                body: JSON.stringify(book)
            });
        }
        resetBookForm();
        loadBooks();
    } catch (error) {
        console.error('Помилка збереження книги:', error);
    }
});

async function editBook(id) {
    const response = await authFetch(`${API_URL}/books/${id}`);
    if (!response) return;
    const book = await response.json();

    document.getElementById('book-id').value       = book.id;
    document.getElementById('book-title').value    = book.title;
    document.getElementById('book-author').value   = book.author;
    document.getElementById('book-isbn').value     = book.isbn || '';
    document.getElementById('book-year').value     = book.year || '';
    document.getElementById('book-quantity').value = book.quantity;
}

async function deleteBook(id) {
    if (confirm('Ви впевнені, що хочете видалити цю книгу?')) {
        await authFetch(`${API_URL}/books/${id}`, { method: 'DELETE' });
        loadBooks();
    }
}

function resetBookForm() {
    document.getElementById('book-form').reset();
    document.getElementById('book-id').value = '';
}




async function loadReaders() {
    try {
        const response = await authFetch(`${API_URL}/readers`);
        if (!response) return;

        const readers = await response.json();
        const tbody = document.querySelector('#readers-table tbody');
        tbody.innerHTML = '';

        readers.forEach(reader => {
            const regDate = reader.registrationDate
                ? new Date(reader.registrationDate).toLocaleDateString('uk-UA')
                : '-';

            const actionsCell = isAdmin()
                ? `<td class="col-actions">
                       <button class="btn-edit" onclick="editReader(${reader.id})">Редагувати</button>
                       <button class="btn-delete" onclick="deleteReader(${reader.id})">Видалити</button>
                   </td>`
                : '';

            tbody.innerHTML += `
                <tr>
                    <td>${reader.id}</td>
                    <td>${reader.fullName}</td>
                    <td>${reader.email || '-'}</td>
                    <td>${reader.phone || '-'}</td>
                    <td>${regDate}</td>
                    ${actionsCell}
                </tr>`;
        });
    } catch (error) {
        console.error('Помилка завантаження читачів:', error);
    }
}

document.getElementById('reader-form').addEventListener('submit', async (e) => {
    e.preventDefault();

    const id = document.getElementById('reader-id').value;
    const reader = {
        fullName: document.getElementById('reader-name').value,
        email:    document.getElementById('reader-email').value,
        phone:    document.getElementById('reader-phone').value
    };

    try {
        if (id) {
            await authFetch(`${API_URL}/readers/${id}`, {
                method: 'PUT',
                body: JSON.stringify(reader)
            });
        } else {
            await authFetch(`${API_URL}/readers`, {
                method: 'POST',
                body: JSON.stringify(reader)
            });
        }
        resetReaderForm();
        loadReaders();
    } catch (error) {
        console.error('Помилка збереження читача:', error);
    }
});

async function editReader(id) {
    const response = await authFetch(`${API_URL}/readers/${id}`);
    if (!response) return;
    const reader = await response.json();

    document.getElementById('reader-id').value    = reader.id;
    document.getElementById('reader-name').value  = reader.fullName;
    document.getElementById('reader-email').value = reader.email || '';
    document.getElementById('reader-phone').value = reader.phone || '';
}

async function deleteReader(id) {
    if (confirm('Ви впевнені, що хочете видалити цього читача?')) {
        await authFetch(`${API_URL}/readers/${id}`, { method: 'DELETE' });
        loadReaders();
    }
}

function resetReaderForm() {
    document.getElementById('reader-form').reset();
    document.getElementById('reader-id').value = '';
}




async function loadLoans() {
    try {
        const response = await authFetch(`${API_URL}/loans`);
        if (!response) return;

        const loans = await response.json();
        const tbody = document.querySelector('#loans-table tbody');
        tbody.innerHTML = '';

        loans.forEach(loan => {
            const statusClass = loan.returned ? 'status-returned' : 'status-active';
            const statusText  = loan.returned ? 'Повернуто' : 'Активна';
            const returnDate  = loan.returnDate
                ? new Date(loan.returnDate).toLocaleDateString('uk-UA')
                : '-';
            const bookTitle   = loan.book   ? loan.book.title      : '-';
            const readerName  = loan.reader ? loan.reader.fullName  : '-';

            const actionsCell = isAdmin()
                ? `<td class="col-actions">
                       ${!loan.returned
                    ? `<button class="btn-return" onclick="returnBook(${loan.id})">Повернути</button>`
                    : ''}
                       <button class="btn-delete" onclick="deleteLoan(${loan.id})">Видалити</button>
                   </td>`
                : '';

            tbody.innerHTML += `
                <tr>
                    <td>${loan.id}</td>
                    <td>${bookTitle}</td>
                    <td>${readerName}</td>
                    <td>${new Date(loan.loanDate).toLocaleDateString('uk-UA')}</td>
                    <td>${returnDate}</td>
                    <td><span class="status-badge ${statusClass}">${statusText}</span></td>
                    ${actionsCell}
                </tr>`;
        });


        loadBooksForLoan();
        loadReadersForLoan();

    } catch (error) {
        console.error('Помилка завантаження видач:', error);
    }
}

async function loadBooksForLoan() {
    const response = await authFetch(`${API_URL}/books`);
    if (!response) return;
    const books = await response.json();

    const select = document.getElementById('loan-book');
    select.innerHTML = '<option value="">Оберіть книгу</option>';
    books.filter(b => b.available > 0).forEach(book => {
        select.innerHTML += `<option value="${book.id}">${book.title} (доступно: ${book.available})</option>`;
    });
}

async function loadReadersForLoan() {
    const response = await authFetch(`${API_URL}/readers`);
    if (!response) return;
    const readers = await response.json();

    const select = document.getElementById('loan-reader');
    select.innerHTML = '<option value="">Оберіть читача</option>';
    readers.forEach(reader => {
        select.innerHTML += `<option value="${reader.id}">${reader.fullName}</option>`;
    });
}

document.getElementById('loan-form').addEventListener('submit', async (e) => {
    e.preventDefault();

    const loan = {
        book_id:   parseInt(document.getElementById('loan-book').value),
        reader_id: parseInt(document.getElementById('loan-reader').value)
    };

    try {
        await authFetch(`${API_URL}/loans`, {
            method: 'POST',
            body: JSON.stringify(loan)
        });
        document.getElementById('loan-form').reset();
        loadLoans();
    } catch (error) {
        console.error('Помилка видачі книги:', error);
    }
});

async function returnBook(id) {
    await authFetch(`${API_URL}/loans/${id}/return`, { method: 'PUT' });
    loadLoans();
}

async function deleteLoan(id) {
    if (confirm('Ви впевнені, що хочете видалити цю видачу?')) {
        await authFetch(`${API_URL}/loans/${id}`, { method: 'DELETE' });
        loadLoans();
    }
}