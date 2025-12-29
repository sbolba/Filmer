package sbolba.film.film.model;

public class Performer {
    private Actor actor;
    private String role;

    public Performer(Actor actor, String role) {
        this.actor = actor;
        this.role = role;
    }

    public Actor getActor() {
        return actor;
    }

    public String getRole() {
        return role;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
