package sbolba.film.film.model;

import java.sql.Date;
import java.sql.Timestamp;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private int id;
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private String bio;
    private Date dateOfBirth;
    private String phoneNumber;
    private Boolean isActive;
    private Boolean isVerified;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp lastLogin;

}
