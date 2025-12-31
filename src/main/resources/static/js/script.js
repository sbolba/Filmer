const API_BASE_URL = '/api/films';

// Check if user is authenticated with retry mechanism
async function checkAuthentication(retries = 3, delay = 200) {
    try {
        const response = await fetch('/api/users/me', {
            credentials: 'same-origin',
            cache: 'no-cache'
        });
        
        if (response.ok) {
            const userData = await response.json();
            document.getElementById('loginBtn').style.display = 'none';
            document.getElementById('userInfo').style.display = 'block';
            document.getElementById('userName').textContent = ` ${userData.firstName || 'User'}`;
            document.getElementById('edit').style.display = 'block';
        } else if (retries > 0) {
            // Retry if not successful and retries remaining
            setTimeout(() => checkAuthentication(retries - 1, delay), delay);
        }
    } catch (error) {
        if (retries > 0) {
            // Retry on error
            setTimeout(() => checkAuthentication(retries - 1, delay), delay);
        } else {
            console.log('User not authenticated');
        }
    }
}

// Call on page load
checkAuthentication();

// Fetch all films from backend
async function fetchAllFilms() {
    try {
        const response = await fetch(API_BASE_URL);
        if (!response.ok) throw new Error('Failed to fetch films');
        const films = await response.json();
        return films;
    } catch (error) {
        console.error('Error fetching films:', error);
        return [];
    }
}

// Fetch films by year
async function fetchFilmsByYear(year) {
    try {
        const response = await fetch(`${API_BASE_URL}/year/${year}`);
        if (!response.ok) throw new Error('Failed to fetch films by year');
        return await response.json();
    } catch (error) {
        console.error('Error fetching films by year:', error);
        return [];
    }
}

// Create a new film
async function createFilm(filmData) {
    try {
        const response = await fetch(API_BASE_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(filmData)
        });
        return response.ok;
    } catch (error) {
        console.error('Error creating film:', error);
        return false;
    }
}

// Delete a film
async function deleteFilm(id) {
    try {
        const response = await fetch(`${API_BASE_URL}/${id}`, {
            method: 'DELETE'
        });
        return response.ok;
    } catch (error) {
        console.error('Error deleting film:', error);
        return false;
    }
}

// Initialize and load films from backend
async function init() {
    const films = await fetchAllFilms();
    // Update your UI with the films data
    console.log('Films from backend:', films);
    renderFilms(films);
}

function renderFilms(films) {
    const grid = document.getElementById('grid');
    const empty = document.getElementById('empty');
    
    if (films.length === 0) {
        empty.style.display = 'block';
        grid.innerHTML = '';
        return;
    }
    
    empty.style.display = 'none';
    grid.innerHTML = '';
    
    films.forEach(film => {
        const card = document.createElement('div');
        card.className = 'card';
        card.innerHTML = `
            <div class="title">${escapeHtml(film.title)}</div>
            <div class="meta">Year: ${film.year} | Duration: ${film.hourDuration}h ${film.minuteDuration}m</div>
            <div class="meta">${escapeHtml(film.resume || '')}</div>
            ${film.performers ? `<div class="badges">${film.performers.map(p => 
                `<span class="badge">${escapeHtml(p.actor.name + ' ' + p.actor.surname)}</span>`
            ).join('')}</div>` : ''}
            <div class="save">Save Film</div>
        `;
        grid.appendChild(card);
    });
}

function escapeHtml(s) {
    return String(s).replace(/[&<>"']/g, c => ({
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#39;'
    }[c]));
}

//Create the director button if the user is logged in and his role is DIRECTOR
/*
async function createDirectorButton() {
    editButton = document.getElementById('edit');
    const role = await getUserRole();
    if (role === 'DIRECTOR') {
        const director = document.createElement('div');
        director.innerHTML = '<div class="director" href="edit.html">Director Panel</div>';
    editButton.appendChild(director);
    }
}

async function getUserRole() {
    try{
        const response = fetch('/user/role');
        if (!response.ok) throw new Error('Failed to fetch user role');
        const role = await response.json();
        return role;
    } catch (error) {
        console.error('Error fetching user role:', error);
        return [];
    }
}
*/

// Call init when page loads
document.addEventListener('DOMContentLoaded', init);

//Handle the mood selection
const selectButton = document.getElementById('selectButton');
const optionsList = document.getElementById('optionsList');
const selectedEmoji = document.getElementById('selectedEmoji');
const selectedText = document.getElementById('selectedText');
const optionItems = document.querySelectorAll('.option-item');

selectButton.addEventListener('click', () => {
            const isOpen = optionsList.classList.contains('open');
            
            if (isOpen) {
                closeDropdown();
            } else {
                openDropdown();
            }
        });

function openDropdown() {
            optionsList.classList.add('open');
            selectButton.classList.add('active');
}

function closeDropdown() {
            optionsList.classList.remove('open');
            selectButton.classList.remove('active');
}  

optionItems.forEach(item => {
            item.addEventListener('click', () => {
                const emoji = item.dataset.emoji;
                const label = item.dataset.label;

                selectedEmoji.textContent = emoji;
                selectedText.textContent = label;

                selectButton.style.justifyContent = 'center';

                optionItems.forEach(opt => opt.classList.remove('selected'));
                item.classList.add('selected');

                closeDropdown();
            });
        });

document.addEventListener('click', (e) => {
    if (!selectButton.contains(e.target) && !optionsList.contains(e.target)) {
        closeDropdown();
    }
});