package sbolba.film.film.model;

import lombok.Data;

@Data
public class Performer {
    private Actor actor;
    private String role;

    public Performer() {
    }

    public Performer(Actor actor, String role) {
        this.actor = actor;
        this.role = role;
    }

}
