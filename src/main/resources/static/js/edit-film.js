const API_BASE_URL = '/api/films';

// Ottieni l'ID del film dall'URL
const urlParams = new URLSearchParams(window.location.search);
const filmId = urlParams.get('id');

// Carica i dati del film quando la pagina si carica
document.addEventListener('DOMContentLoaded', async () => {
    if (!filmId) {
        alert('ID film non trovato');
        window.location.href = '/home.html';
        return;
    }
    
    await loadFilmData();
    
    document.getElementById('editForm').addEventListener('submit', handleSubmit);
});

// Carica i dati del film dal backend
async function loadFilmData() {
    try {
        const response = await fetch(`${API_BASE_URL}/${filmId}`);
        if (!response.ok) throw new Error('Film non trovato');
        
        const film = await response.json();
        
        document.getElementById('filmId').value = film.id;
        document.getElementById('title').value = film.title;
        document.getElementById('year').value = film.year;
        document.getElementById('hourDuration').value = film.hourDuration;
        document.getElementById('minuteDuration').value = film.minuteDuration;
        document.getElementById('resume').value = film.resume || '';
    } catch (error) {
        console.error('Errore nel caricamento del film:', error);
        alert('Errore nel caricamento dei dati del film');
        window.location.href = '/home.html';
    }
}

// Gestisci il submit del form
async function handleSubmit(event) {
    event.preventDefault();
    
    const filmData = {
        id: parseInt(document.getElementById('filmId').value),
        title: document.getElementById('title').value,
        year: parseInt(document.getElementById('year').value),
        hourDuration: parseInt(document.getElementById('hourDuration').value),
        minuteDuration: parseInt(document.getElementById('minuteDuration').value),
        resume: document.getElementById('resume').value
    };
    
    try {
        const response = await fetch(`${API_BASE_URL}/${filmId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(filmData)
        });
        
        if (response.ok) {
            alert('Film aggiornato con successo!');
            window.location.href = '/home.html';
        } else {
            alert('Errore nell\'aggiornamento del film');
        }
    } catch (error) {
        console.error('Errore:', error);
        alert('Errore nella connessione al server');
    }
}