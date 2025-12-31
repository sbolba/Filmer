package sbolba.film.film.model;

import lombok.Data;

@Data
public class Role {
    private int id;
    private String name;
    private String description;

    public Role(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

}
