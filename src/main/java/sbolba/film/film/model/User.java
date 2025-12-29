package sbolba.film.film.model;

import java.util.Objects;

public class User {

    private int id;
    private String username;
    private String password;

    public User(int id, String username, String password) {
        Objects.requireNonNull(id, "id is null");
        Objects.requireNonNull(username, "username is null");
        Objects.requireNonNull(password, "password is null");
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }   

    public void setPassword(String password) {
        this.password = password;
    }

}
