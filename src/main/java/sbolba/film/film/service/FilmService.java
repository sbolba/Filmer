package sbolba.film.film.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sbolba.film.film.model.*;
import sbolba.film.film.repository.FilmRepository;

import java.util.List;

@Service
public class FilmService {

    @Autowired
    private FilmRepository filmRepository;

    public List<Film> getAllFilms() {
        return filmRepository.findAll();
    }

    public Film getFilmById(int id) {
        return filmRepository.findById(id);
    }

    public boolean createFilm(Film film) {
        return filmRepository.save(film);
    }

    public boolean updateFilm(Film film) {
        if (filmRepository.findById(film.getId()) != null) {
            return filmRepository.update(film);
        }
        return false;
    }

    public boolean deleteFilm(int id) {
        return filmRepository.deleteById(id);
    }

    public List<Film> getFilmsByYear(int year) {
        return filmRepository.findByYear(year);
    }

    public List<Film> getFilmsByActor(List<Actor> actors) {
        return filmRepository.findByActor(actors);
    }

    //TODO: the mood function has to be implemented after I set a column "Mood" in the film table in the database
    /*
    public List<Film> getFilmsByMood(Mood mood){
        return filmRepository.findByMood(mood);
    }
    */
}
