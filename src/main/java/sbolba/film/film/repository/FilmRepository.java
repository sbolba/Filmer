package sbolba.film.film.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import sbolba.film.film.model.Actor;
import sbolba.film.film.model.Film;
import sbolba.film.film.model.Performer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

@Repository
public class FilmRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Performer> PerformerRowMapper = new RowMapper<Performer>() {
        @Override
        public Performer mapRow(ResultSet rs, int rowNum) throws SQLException {
            Performer p = new Performer(
                new Actor(
                rs.getString("actorname"),
                rs.getString("actorsurname"),
                rs.getDate("actordateofbirth")),
                rs.getString("role")
            );
            return p;
        }
    };

    private final RowMapper<Film> filmRowMapper = new RowMapper<Film>() {
        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {

            String sql = "SELECT actorname, actorsurname, actordateofbirth, role FROM films.performance WHERE idfilm = ?";
            List<Performer> performers = jdbcTemplate.query(sql, PerformerRowMapper, rs.getInt("idfilm"));

            Film film = new Film(
                rs.getInt("idfilm"),
                rs.getString("title"),
                rs.getInt("year"),
                rs.getInt("hourduration"),
                rs.getInt("minuteduration"),
                performers,
                rs.getString("resume")
            );
            return film;
        }
    };

    public List<Film> findAll() {
        String sql = "SELECT * FROM films.film";
        return jdbcTemplate.query(sql, filmRowMapper);
    }

    public Film findById(int id) {
        String sql = "SELECT * FROM films.film WHERE idfilm = ?";
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, id);
        return films.isEmpty() ? null : films.get(0);
    }

    //without performers list
    public boolean save(Film film) {
        String sql = "INSERT INTO films.film (title, year, hourduration, minuteduration, resume) VALUES (?, ?, ?, ?, ?)";
        
        int rowsAffected = jdbcTemplate.update(sql, 
            film.getTitle(), 
            film.getYear(), 
            film.getHourDuration(), 
            film.getMinuteDuration(), 
            film.getResume()
        );
        
        if (rowsAffected > 0) {
            return true;
        }
        return false;
    }

    public boolean update(Film film) {
        String sql = "UPDATE films.film SET title = ?, year = ?, hourduration = ?, minuteduration = ?, resume = ? WHERE idfilm = ?";
        int rowsAffected = jdbcTemplate.update(sql, 
            film.getTitle(), 
            film.getYear(), 
            film.getHourDuration(), 
            film.getMinuteDuration(), 
            film.getResume(),
            film.getId()
        );
        return rowsAffected > 0;
    }

    public boolean deleteById(int id) {
        String sql = "DELETE FROM films.film WHERE idfilm = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        return rowsAffected > 0;
    }

    public List<Film> findByYear(int year) {
        String sql = "SELECT * FROM films.film WHERE year = ?";
        return jdbcTemplate.query(sql, filmRowMapper, year);
    }

    public List<Film> findByActor(List<Actor> actors) {
        List<Film> results = new ArrayList<>();
        for (Actor actor : actors) {
            String sql = "SELECT f.* FROM films.film f JOIN films.performance p ON f.idfilm = p.idfilm WHERE LOWER(p.actorname) = LOWER(?) AND LOWER(p.actorsurname) = LOWER(?) AND LOWER(p.actordateofbirth) = LOWER(?)";
            results.addAll(jdbcTemplate.query(sql, filmRowMapper, actor.getName(), actor.getSurname(), actor.getDateOfBirth()));
        }
        return results;
    }

    //TODO: the mood function has to be implemented after I set a column "Mood" in the film table in the database
    /*public List<Film> findByMood(Mood mood){
    }
    */
}
