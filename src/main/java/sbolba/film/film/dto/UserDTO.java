package sbolba.film.film.dto;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.NoArgsConstructor;
import sbolba.film.film.model.User;
import sbolba.film.film.model.Role;

@Data
@NoArgsConstructor
public class UserDTO {
    
    private Integer id;
    private String username;
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
    private List<String> roles;  // Solo i nomi dei ruoli
    
    //  *NON includiamo la password per sicurezza!
    
    public UserDTO(User user, List<Role> roles) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.avatarUrl = user.getAvatarUrl();
        this.bio = user.getBio();
        this.dateOfBirth = user.getDateOfBirth();
        this.phoneNumber = user.getPhoneNumber();
        this.isActive = user.getIsActive();
        this.isVerified = user.getIsVerified();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
        this.lastLogin = user.getLastLogin();
        // Converti List<Role> in List<String> (solo i nomi)
        this.roles = roles.stream()
                          .map(Role::getName)
                          .collect(Collectors.toList());
    }

}