/**
 * app.js - Frontend logic for CurrencyXchange
 * Handles UI interactions, theme switching, and Fetch API calls with Basic Auth.
 */

// -----------------------------------------------------------------------------
// DOM Elements
// -----------------------------------------------------------------------------
const DOM = {
    themeBtn: document.getElementById('theme-toggle'),
    loginContainer: document.getElementById('login-container'),
    appContainer: document.getElementById('app-container'),
    
    // Login
    usernameInput: document.getElementById('username'),
    passwordInput: document.getElementById('password'),
    loginBtn: document.getElementById('login-btn'),
    logoutBtn: document.getElementById('logout-btn'),
    loginError: document.getElementById('login-error'),
    
    // Converter
    amountInput: document.getElementById('amount'),
    fromCurrency: document.getElementById('from-currency'),
    toCurrency: document.getElementById('to-currency'),
    swapBtn: document.getElementById('swap-btn'),
    convertBtn: document.getElementById('convert-btn'),
    convertError: document.getElementById('convert-error'),
    
    // Result
    resultArea: document.getElementById('result-area'),
    resultValue: document.getElementById('result-value'),
    resultCurrency: document.getElementById('result-currency'),
    rateFrom: document.getElementById('rate-from'),
    rateTo: document.getElementById('rate-to'),
    rateValue: document.getElementById('rate-value'),
    
    // History
    historyList: document.getElementById('history-list'),
    refreshHistoryBtn: document.getElementById('refresh-history-btn')
};

// -----------------------------------------------------------------------------
// State & Constants
// -----------------------------------------------------------------------------
const API_BASE = '/api';
let authCredentials = null;

// -----------------------------------------------------------------------------
// Initialization & Theme
// -----------------------------------------------------------------------------
function init() {
    initTheme();
    checkAuthStatus();
    setupEventListeners();
}

function initTheme() {
    const isDark = localStorage.getItem('theme') === 'dark';
    if (isDark) document.body.setAttribute('data-theme', 'dark');
    DOM.themeBtn.textContent = isDark ? '☀️' : '🌙';
}

DOM.themeBtn.addEventListener('click', () => {
    const isDark = document.body.hasAttribute('data-theme');
    if (isDark) {
        document.body.removeAttribute('data-theme');
        localStorage.setItem('theme', 'light');
        DOM.themeBtn.textContent = '🌙';
    } else {
        document.body.setAttribute('data-theme', 'dark');
        localStorage.setItem('theme', 'dark');
        DOM.themeBtn.textContent = '☀️';
    }
});

// -----------------------------------------------------------------------------
// Authentication
// -----------------------------------------------------------------------------
function checkAuthStatus() {
    const storedAuth = sessionStorage.getItem('basicAuth');
    if (storedAuth) {
        authCredentials = storedAuth;
        showDashboard();
    } else {
        showLogin();
    }
}

function showLogin() {
    DOM.loginContainer.classList.add('active');
    DOM.loginContainer.classList.remove('hidden');
    DOM.appContainer.classList.add('hidden');
    DOM.appContainer.classList.remove('active');
    
    // Clear inputs
    DOM.usernameInput.value = '';
    DOM.passwordInput.value = '';
    DOM.loginError.textContent = '';
}

function showDashboard() {
    DOM.loginContainer.classList.add('hidden');
    DOM.loginContainer.classList.remove('active');
    DOM.appContainer.classList.add('active');
    DOM.appContainer.classList.remove('hidden');
    
    // Load initial data
    loadCurrencies();
    loadHistory();
}

DOM.loginBtn.addEventListener('click', async () => {
    const username = DOM.usernameInput.value.trim();
    const password = DOM.passwordInput.value.trim();
    
    if (!username || !password) {
        DOM.loginError.textContent = 'Please enter both username and password';
        return;
    }
    
    DOM.loginBtn.textContent = 'Logging in...';
    DOM.loginBtn.disabled = true;
    
    const token = btoa(`${username}:${password}`); // Basic auth base64 encode
    
    try {
        // Test auth by fetching currencies
        const res = await fetch(`${API_BASE}/currencies`, {
            headers: { 'Authorization': `Basic ${token}` }
        });
        
        if (res.ok) {
            authCredentials = token;
            sessionStorage.setItem('basicAuth', token);
            showDashboard();
        } else {
            DOM.loginError.textContent = 'Invalid credentials. Try testuser / password123';
        }
    } catch (err) {
        DOM.loginError.textContent = 'Network error connecting to server.';
    } finally {
        DOM.loginBtn.textContent = 'Login';
        DOM.loginBtn.disabled = false;
    }
});

DOM.logoutBtn.addEventListener('click', () => {
    authCredentials = null;
    sessionStorage.removeItem('basicAuth');
    
    // Clear result area
    DOM.resultArea.classList.add('hidden');
    DOM.amountInput.value = '1.00';
    
    showLogin();
});

// -----------------------------------------------------------------------------
// API Calls & Interactions
// -----------------------------------------------------------------------------
async function apiCall(endpoint, options = {}) {
    if (!authCredentials) throw new Error('Not authenticated');
    
    const headers = {
        'Authorization': `Basic ${authCredentials}`,
        'Content-Type': 'application/json',
        ...options.headers
    };
    
    const res = await fetch(`${API_BASE}${endpoint}`, { ...options, headers });
    
    if (res.status === 401) {
        DOM.logoutBtn.click(); // Auto logout on 401
        throw new Error('Session expired');
    }
    
    const data = await res.json();
    if (!res.ok) throw new Error(data.message || 'API Error');
    return data;
}

// 1. Load Currencies for Dropdowns
async function loadCurrencies() {
    try {
        const currencies = await apiCall('/currencies');
        if (currencies && currencies.length > 0) {
            // Sort alphabetically
            currencies.sort();
            
            // Build options HTML
            const getFlag = (code) => {
                // Simplistic flag mapping for demo purposes. Real app would use a library.
                const map = { USD: '🇺🇸', EUR: '🇪🇺', GBP: '🇬🇧', INR: '🇮🇳', AUD: '🇦🇺', CAD: '🇨🇦', JPY: '🇯🇵' };
                return map[code] || '🌐';
            };
            
            let optionsHTML = '';
            currencies.forEach(code => {
                optionsHTML += `<option value="${code}">${getFlag(code)} ${code}</option>`;
            });
            
            DOM.fromCurrency.innerHTML = optionsHTML;
            DOM.toCurrency.innerHTML = optionsHTML;
            
            // Set defaults
            DOM.fromCurrency.value = 'USD';
            DOM.toCurrency.value = 'INR';
        }
    } catch (error) {
        console.error('Failed to load currencies', error);
    }
}

// 2. Perform Conversion
DOM.convertBtn.addEventListener('click', async () => {
    const amount = parseFloat(DOM.amountInput.value);
    const from = DOM.fromCurrency.value;
    const to = DOM.toCurrency.value;
    
    if (isNaN(amount) || amount <= 0) {
        DOM.convertError.textContent = 'Please enter a valid amount greater than 0';
        return;
    }
    
    DOM.convertError.textContent = '';
    DOM.convertBtn.textContent = 'Converting...';
    DOM.convertBtn.disabled = true;
    
    try {
        const result = await apiCall('/convert', {
            method: 'POST',
            body: JSON.stringify({ fromCurrency: from, toCurrency: to, amount: amount })
        });
        
        // Display result
        DOM.resultValue.textContent = formatCurrency(result.convertedAmount);
        DOM.resultCurrency.textContent = to;
        DOM.rateFrom.textContent = from;
        DOM.rateTo.textContent = to;
        DOM.rateValue.textContent = result.exchangeRate.toFixed(4);
        
        DOM.resultArea.classList.remove('hidden');
        
        // Refresh history immediately
        loadHistory();
        
    } catch (error) {
        DOM.convertError.textContent = error.message;
        DOM.resultArea.classList.add('hidden');
    } finally {
        DOM.convertBtn.textContent = 'Convert';
        DOM.convertBtn.disabled = false;
    }
});

// 3. Swap Currencies
DOM.swapBtn.addEventListener('click', () => {
    const temp = DOM.fromCurrency.value;
    DOM.fromCurrency.value = DOM.toCurrency.value;
    DOM.toCurrency.value = temp;
    
    // Rotate animation on button
    DOM.swapBtn.style.transform = 'rotate(180deg)';
    setTimeout(() => DOM.swapBtn.style.transform = 'none', 300);
});

// 4. Load History
async function loadHistory() {
    try {
        const history = await apiCall('/history');
        
        if (history.length === 0) {
            DOM.historyList.innerHTML = `
                <div class="empty-state">
                    <span class="icon">📋</span>
                    <p>No conversions yet.</p>
                </div>
            `;
            return;
        }
        
        // Build HTML
        let html = '';
        history.forEach(item => {
            const date = new Date(item.timestamp).toLocaleString();
            html += `
                <div class="history-item">
                    <div>
                        <div class="history-route">${item.fromCurrency} ➔ ${item.toCurrency}</div>
                        <div class="history-date">${date}</div>
                    </div>
                    <div class="history-amount">
                        ${formatCurrency(item.convertedAmount)} <span style="font-size: 0.8rem; color: var(--text-secondary);">${item.toCurrency}</span>
                    </div>
                </div>
            `;
        });
        
        DOM.historyList.innerHTML = html;
        
    } catch (error) {
        console.error('Failed to load history', error);
    }
}

DOM.refreshHistoryBtn.addEventListener('click', loadHistory);

// Utility
function formatCurrency(value) {
    return new Intl.NumberFormat('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(value);
}

// Start app
init();
