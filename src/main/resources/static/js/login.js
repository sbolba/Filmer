const container = document.getElementById('container');
const registerBtn = document.getElementById('register');
const loginBtn = document.getElementById('login');

registerBtn.addEventListener('click', () => {
    container.classList.add("active");
    resetForms();
});

loginBtn.addEventListener('click', () => {
    container.classList.remove("active");
    resetForms();
});

// Function to reset form inputs
function resetForms() {
    document.querySelector('.sign-in form').reset();
    document.querySelector('.sign-up form').reset();
}

// Function to show error message
function showError(message) {
    const errorDiv = document.createElement('div');
    errorDiv.className = 'error-banner';
    errorDiv.innerHTML = `<span>⚠️</span><span>${message}</span>`;
    document.body.appendChild(errorDiv);
    
    setTimeout(() => {
        errorDiv.classList.add('fade-out');
        setTimeout(() => errorDiv.remove(), 300);
    }, 4000);
}

// Function to show success message
function showSuccess(message) {
    const successDiv = document.createElement('div');
    successDiv.className = 'success-banner';
    successDiv.innerHTML = `<span>✓</span><span>${message}</span>`;
    document.body.appendChild(successDiv);
    
    setTimeout(() => {
        successDiv.classList.add('fade-out');
        setTimeout(() => successDiv.remove(), 300);
    }, 3000);
}

// Gestione registrazione
document.querySelector('.sign-up form').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const name = e.target.querySelector('input[type="text"]').value;
    const email = e.target.querySelector('input[type="email"]').value;
    const password = e.target.querySelector('input[type="password"]').value;
    
    try {
        const response = await fetch('/api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, password })
        });
        
        const data = await response.json();
        
        if (response.ok) {
            showSuccess('Account created successfully! Please sign in.');
            container.classList.remove("active");
            e.target.reset();
        } else {
            showError(data.error || 'Registration failed. Please try again.');
        }
    } catch (error) {
        showError('Unable to connect to server. Please check your connection.');
    }
});

// Gestione login
document.querySelector('.sign-in form').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const email = e.target.querySelector('input[type="email"]').value;
    const password = e.target.querySelector('input[type="password"]').value;
    
    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        
        const data = await response.json();
        
        if (response.ok) {
            window.location.href = '/home.html';
        } else {
            showError(data.error || 'Login failed. Please check your credentials.');
        }
    } catch (error) {
        showError('Unable to connect to server. Please check your connection.');
    }
});

// Gestione errore OAuth2 login
const urlParams = new URLSearchParams(window.location.search);
if (urlParams.get('error') === 'true') {
    showError('OAuth login failed. Please try again or use email/password.');
    window.history.replaceState({}, document.title, window.location.pathname);
}