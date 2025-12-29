package sbolba.film.film.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sbolba.film.film.model.Film;
import sbolba.film.film.model.Actor;

import sbolba.film.film.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/api/films")
public class FilmController {

    @Autowired
    private FilmService filmService;

    // GET all films
    @GetMapping
    public ResponseEntity<List<Film>> getAllFilms() {
        List<Film> films = filmService.getAllFilms();
        return ResponseEntity.ok(films);
    }

    // GET film by ID
    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilmById(@PathVariable int id) {
        Film film = filmService.getFilmById(id);
        if (film != null) {
            return ResponseEntity.ok(film);
        }
        return ResponseEntity.notFound().build();
    }

    // POST create new film
    @PostMapping
    public boolean createFilm(@RequestBody Film film) {
        return filmService.createFilm(film);
    }

    // PUT update film
    @PutMapping("/update/{film}")
    public boolean updateFilm(@PathVariable Film film) {
        return filmService.updateFilm(film);
    }

    // DELETE film
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFilm(@PathVariable int id) {
        boolean deleted = filmService.deleteFilm(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // GET films by year
    @GetMapping("/year/{year}")
    public ResponseEntity<List<Film>> getFilmsByYear(@PathVariable int year) {
        List<Film> films = filmService.getFilmsByYear(year);
        return ResponseEntity.ok(films);
    }

    // GET films by actors
    @GetMapping("/actors")
    public ResponseEntity<List<Film>> getFilmsByActors(@RequestBody List<Actor> actors){
        List<Film> films = filmService.getFilmsByActor(actors);
        return ResponseEntity.ok(films);
    }
}
