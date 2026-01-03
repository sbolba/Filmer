package sbolba.film.film.model;

import java.util.Date;

import lombok.Data;

@Data
public class Actor {

    private String name;
    private String surname;
    private Date dateOfBirth;
    private String role;

    public Actor() {
    }

    public Actor(String name, String surname, Date dateOfBirth) {
        this.name = name;
        this.surname = surname;
        this.dateOfBirth = dateOfBirth;
    }

    public Actor(String name, String surname, Date dateOfBirth, String role) {
        this.name = name;
        this.surname = surname;
        this.dateOfBirth = dateOfBirth;
        this.role = role;
    }
    
}
