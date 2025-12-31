package sbolba.film.film.model;

import java.util.List;
import java.util.Objects;

import lombok.Data;

@Data
public class Film {
    private int id;
    private String title;
    private int year;
    private int hourDuration;
    private int minuteDuration;
    private List<Performer> performers;
    private String resume;

    public Film() {
    }

    public Film(int id, String title, int year, int hourDuration, int minuteDuration) {
        Objects.requireNonNull(id, "id is null");
        Objects.requireNonNull(title, "title is null");
        Objects.requireNonNull(year, "year is null");
        Objects.requireNonNull(hourDuration, "hourDuration is null");
        Objects.requireNonNull(minuteDuration, "minuteDuration is null");
        this.id = id;
        this.title = title;
        this.year = year;
        this.hourDuration = hourDuration;
        this.minuteDuration = minuteDuration;
    }

    public Film(int id, String title, int year, int hourDuration, int minuteDuration, List<Performer> performers) {
        Objects.requireNonNull(id, "id is null");
        Objects.requireNonNull(title, "title is null");
        Objects.requireNonNull(year, "year is null");
        Objects.requireNonNull(hourDuration, "hourDuration is null");
        Objects.requireNonNull(minuteDuration, "minuteDuration is null");
        Objects.requireNonNull(performers, "performers is null");
        this.id = id;
        this.title = title;
        this.year = year;
        this.hourDuration = hourDuration;
        this.minuteDuration = minuteDuration;
        this.performers = performers;
    }

    public Film(int id, String title, int year, int hourDuration, int minuteDuration, String resume) {
        Objects.requireNonNull(id, "id is null");
        Objects.requireNonNull(title, "title is null");
        Objects.requireNonNull(year, "year is null");
        Objects.requireNonNull(hourDuration, "hourDuration is null");
        Objects.requireNonNull(minuteDuration, "minuteDuration is null");
        Objects.requireNonNull(resume, "resume is null");
        this.id = id;
        this.title = title;
        this.year = year;
        this.hourDuration = hourDuration;
        this.minuteDuration = minuteDuration;
        this.resume = resume;
    }

    public Film(int id, String title, int year, int hourDuration, int minuteDuration, List<Performer> performers, String resume) {
        Objects.requireNonNull(id, "id is null");
        Objects.requireNonNull(title, "title is null");
        Objects.requireNonNull(year, "year is null");
        Objects.requireNonNull(hourDuration, "hourDuration is null");
        Objects.requireNonNull(minuteDuration, "minuteDuration is null");
        Objects.requireNonNull(performers, "performers is null");
        Objects.requireNonNull(resume, "resume is null");
        this.id = id;
        this.title = title;
        this.year = year;
        this.hourDuration = hourDuration;
        this.minuteDuration = minuteDuration;
        this.performers = performers;
        this.resume = resume;
    }

}
