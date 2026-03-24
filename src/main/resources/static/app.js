const API_BASE_URL = '/api/v1';

// App State
let appState = {
    token: localStorage.getItem('token'),
    userId: localStorage.getItem('userId'),
    username: localStorage.getItem('username'),
    role: localStorage.getItem('role'),
    books: [],
    borrowings: []
};

// DOM Elements
const authView = document.getElementById('auth-view');
const appView = document.getElementById('app-view');
const authForm = document.getElementById('auth-form');
const usernameInput = document.getElementById('username');
const passwordInput = document.getElementById('password');
const authModeText = document.getElementById('auth-mode-text');
const toggleAuthMode = document.getElementById('toggle-auth-mode');
const authSubmitBtn = document.getElementById('auth-submit');

const toast = document.getElementById('toast');
const toastIcon = document.getElementById('toast-icon');
const toastMessage = document.getElementById('toast-message');

const sidebarNav = document.querySelectorAll('.nav-item');
const contentSections = document.querySelectorAll('.content-section');
const logoutBtn = document.getElementById('logout-btn');

let isLoginMode = true;
let selectedBookId = null;

// Initialize
function init() {
    if (appState.token) {
        showAppView();
    } else {
        showAuthView();
    }
}

// UI Helpers
function showToast(message, type = 'success') {
    toast.className = `toast show ${type}`;
    toastMessage.textContent = message;
    toastIcon.innerHTML = type === 'success' ? '<i class="fas fa-check-circle"></i>' : '<i class="fas fa-exclamation-circle"></i>';
    
    setTimeout(() => {
        toast.className = 'toast hidden';
    }, 3000);
}

function showAuthView() {
    authView.classList.remove('hidden');
    appView.classList.add('hidden');
}

function showAppView() {
    authView.classList.add('hidden');
    appView.classList.remove('hidden');
    
    document.getElementById('current-username').textContent = appState.username;
    document.getElementById('current-role').textContent = appState.role;
    
    if (appState.role === 'ADMIN') {
        document.querySelector('.admin-only').style.display = 'flex';
    } else {
        document.querySelector('.admin-only').style.display = 'none';
    }
    
    loadBooks();
    loadBorrowings();
}

// Auth Logic
toggleAuthMode.addEventListener('click', (e) => {
    e.preventDefault();
    isLoginMode = !isLoginMode;
    if (isLoginMode) {
        authSubmitBtn.textContent = 'Login';
        authModeText.textContent = "Don't have an account?";
        toggleAuthMode.textContent = 'Register now';
    } else {
        authSubmitBtn.textContent = 'Register';
        authModeText.textContent = "Already have an account?";
        toggleAuthMode.textContent = 'Login';
    }
});

authForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const endpoint = isLoginMode ? '/auth/login' : '/auth/register';
    
    authSubmitBtn.disabled = true;
    authSubmitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Processing...';
    
    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                username: usernameInput.value,
                password: passwordInput.value
            })
        });
        
        const data = await response.json();
        
        if (response.ok) {
            appState = {
                token: data.token,
                userId: data.id,
                username: data.username,
                role: data.role
            };
            localStorage.setItem('token', data.token);
            localStorage.setItem('userId', data.id);
            localStorage.setItem('username', data.username);
            localStorage.setItem('role', data.role);
            
            showToast('Authentication successful!');
            showAppView();
            usernameInput.value = '';
            passwordInput.value = '';
        } else {
            showToast(data.message || 'Authentication failed', 'error');
        }
    } catch (error) {
        showToast('Server error. Please try again.', 'error');
    } finally {
        authSubmitBtn.disabled = false;
        authSubmitBtn.textContent = isLoginMode ? 'Login' : 'Register';
    }
});

logoutBtn.addEventListener('click', () => {
    localStorage.clear();
    appState = { token: null, userId: null, username: null, role: null };
    showAuthView();
});

// Navigation
sidebarNav.forEach(item => {
    item.addEventListener('click', (e) => {
        e.preventDefault();
        sidebarNav.forEach(nav => nav.classList.remove('active'));
        item.classList.add('active');
        
        const targetId = item.getAttribute('data-target');
        contentSections.forEach(section => {
            section.classList.remove('active');
            section.classList.add('hidden');
            if (section.id === targetId) {
                section.classList.remove('hidden');
                section.classList.add('active');
            }
        });
        
        if (targetId === 'my-books-section') {
            loadBorrowings();
        } else if (targetId === 'dashboard-section') {
            loadBooks();
        }
    });
});

// fetch with Auth
async function fetchWithAuth(url, options = {}) {
    if (!options.headers) options.headers = {};
    options.headers['Authorization'] = `Bearer ${appState.token}`;
    
    const response = await fetch(url, options);
    if (response.status === 401 || response.status === 403) {
        logoutBtn.click();
        throw new Error('Unauthorized');
    }
    return response;
}

// Load Books
async function loadBooks() {
    const grid = document.getElementById('books-grid');
    try {
        const response = await fetchWithAuth(`${API_BASE_URL}/books`);
        if (response.ok) {
            const data = await response.json();
            // Spring data page returns content array
            const books = data.content || data;
            appState.books = books;
            renderBooks(books);
        }
    } catch (e) {
        grid.innerHTML = '<div class="loader">Failed to load books.</div>';
    }
}

function renderBooks(books) {
    const grid = document.getElementById('books-grid');
    grid.innerHTML = '';
    
    if (books.length === 0) {
        grid.innerHTML = '<div class="loader">No books available at the moment.</div>';
        return;
    }
    
    books.forEach(book => {
        const isAvailable = book.stockDisponible > 0;
        const card = document.createElement('div');
        card.className = 'book-card';
        card.innerHTML = `
            <div class="book-cover-placeholder">
                <i class="fas fa-book"></i>
            </div>
            <div class="book-info">
                <h3>${book.title}</h3>
                <p class="author">${book.authorName}</p>
                <div class="stock-badge ${isAvailable ? 'stock-available' : 'stock-out'}">
                    ${isAvailable ? book.stockDisponible + ' Available' : 'Out of Stock'}
                </div>
            </div>
        `;
        card.addEventListener('click', () => openBookModal(book));
        grid.appendChild(card);
    });
}

// Modal
const modalOverlay = document.getElementById('modal-overlay');
const bookModal = document.getElementById('book-modal');
const modalCloseBtns = document.querySelectorAll('.modal-close');
const btnBorrow = document.getElementById('btn-borrow');

function openBookModal(book) {
    selectedBookId = book.id;
    document.getElementById('detail-title').textContent = book.title;
    document.getElementById('detail-author').textContent = book.authorName;
    document.getElementById('detail-isbn').textContent = `ISBN: ${book.isbn}`;
    
    const isAvailable = book.stockDisponible > 0;
    const stockEl = document.getElementById('detail-stock');
    stockEl.textContent = isAvailable ? `Stock: ${book.stockDisponible}` : 'Out of Stock';
    stockEl.className = `stock-badge ${isAvailable ? 'stock-available' : 'stock-out'}`;
    
    btnBorrow.disabled = !isAvailable;
    
    modalOverlay.style.display = 'block';
    bookModal.style.display = 'block';
    setTimeout(() => {
        modalOverlay.classList.add('show');
        bookModal.classList.add('show');
    }, 10);
}

function closeModal() {
    modalOverlay.classList.remove('show');
    bookModal.classList.remove('show');
    setTimeout(() => {
        modalOverlay.style.display = 'none';
        bookModal.style.display = 'none';
    }, 300);
}

modalCloseBtns.forEach(btn => btn.addEventListener('click', closeModal));

// Borrow Book
btnBorrow.addEventListener('click', async () => {
    try {
        btnBorrow.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';
        const response = await fetchWithAuth(`${API_BASE_URL}/borrowings/checkout`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                bookId: selectedBookId,
                userId: appState.userId
            })
        });
        
        if (response.ok) {
            showToast('Book borrowed successfully!');
            closeModal();
            loadBooks();
        } else {
            const err = await response.json();
            showToast(err.message || 'Failed to borrow book', 'error');
        }
    } catch (e) {
        showToast('Error borrowing book', 'error');
    } finally {
        btnBorrow.innerHTML = 'Borrow Book';
    }
});

// Load Borrowings
async function loadBorrowings() {
    const list = document.getElementById('borrowings-list');
    list.innerHTML = '<div class="loader"><i class="fas fa-spinner fa-spin"></i> Loading...</div>';
    try {
        const response = await fetchWithAuth(`${API_BASE_URL}/borrowings/user/${appState.userId}`);
        if (response.ok) {
            const data = await response.json();
            renderBorrowings(data);
        }
    } catch (e) {
        list.innerHTML = '<div class="loader">Failed to load borrowings.</div>';
    }
}

function renderBorrowings(borrowings) {
    const list = document.getElementById('borrowings-list');
    list.innerHTML = '';
    
    if (borrowings.length === 0) {
        list.innerHTML = '<div class="loader">You have no borrowings.</div>';
        return;
    }
    
    borrowings.forEach(b => {
        const card = document.createElement('div');
        card.className = 'borrowing-card';
        const d = new Date(b.borrowDate).toLocaleDateString();
        const due = new Date(b.dueDate).toLocaleDateString();
        
        card.innerHTML = `
            <div class="borrowing-details">
                <h3>Book ID: ${b.bookId}</h3>
                <div class="borrowing-meta">
                    <span><i class="fas fa-calendar-alt"></i> Borrowed: ${d}</span>
                    <span><i class="fas fa-clock"></i> Due: ${due}</span>
                </div>
            </div>
            <div>
                ${b.status === 'ACTIVE' 
                    ? `<button class="btn btn-primary" onclick="returnBook(${b.id})">Return</button>`
                    : `<span class="status-badge status-returned">Returned</span>`
                }
            </div>
        `;
        list.appendChild(card);
    });
}

// Return Book
window.returnBook = async function(borrowingId) {
    try {
        const response = await fetchWithAuth(`${API_BASE_URL}/borrowings/return/${borrowingId}`, {
            method: 'POST'
        });
        if (response.ok) {
            showToast('Book returned successfully!');
            loadBorrowings();
            loadBooks();
        } else {
            showToast('Failed to return book', 'error');
        }
    } catch (e) {
        showToast('Error returning book', 'error');
    }
};

// Add Book (Admin)
document.getElementById('add-book-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const btn = e.target.querySelector('button');
    btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';
    
    try {
        const response = await fetchWithAuth(`${API_BASE_URL}/books`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                title: document.getElementById('new-book-title').value,
                isbn: document.getElementById('new-book-isbn').value,
                authorId: parseInt(document.getElementById('new-book-author').value),
                stockDisponible: parseInt(document.getElementById('new-book-stock').value),
                categoryIds: []
            })
        });
        
        if (response.ok) {
            showToast('Book added successfully!');
            e.target.reset();
            loadBooks();
        } else {
            showToast('Failed to add book', 'error');
        }
    } catch (e) {
        showToast('Error adding book', 'error');
    } finally {
        btn.innerHTML = 'Add Book';
    }
});

// Search functionality
document.getElementById('book-search').addEventListener('input', (e) => {
    const q = e.target.value.toLowerCase();
    const filtered = appState.books.filter(b => 
        b.title.toLowerCase().includes(q) || 
        b.authorName.toLowerCase().includes(q) ||
        b.isbn.toLowerCase().includes(q)
    );
    renderBooks(filtered);
});

// Run Init
init();
