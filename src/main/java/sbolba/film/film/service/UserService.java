package sbolba.film.film.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import sbolba.film.film.model.User;
import sbolba.film.film.dto.UserDTO;
import sbolba.film.film.model.Role;
import sbolba.film.film.repository.UserRepository;
import sbolba.film.film.repository.RoleRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDTO getUserProfile(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return null;
        }
        
        List<Role> roles = roleRepository.findByUserId(user.getId());
        return new UserDTO(user, roles);
    }

    public List<UserDTO> getUsersByRole(String roleName) {
        List<User> users = userRepository.findByRole(roleName);
        
        return users.stream()
                    .map(user -> {
                        List<Role> roles = roleRepository.findByUserId(user.getId());
                        return new UserDTO(user, roles);
                    })
                    .collect(Collectors.toList());
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean userHasRole(String username, String roleName) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return false;
        }
        
        return roleRepository.userHasRole(user.getId(), roleName);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User registerUser(String name, String email, String password) {
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setUsername(null); // Username opzionale per manual registration
        
        // Hash password con BCrypt
        String hashedPassword = passwordEncoder.encode(password);
        newUser.setPassword(hashedPassword);
        
        // Dividi nome in firstName e lastName
        String[] nameParts = name.trim().split(" ", 2);
        newUser.setFirstName(nameParts[0]);
        newUser.setLastName(nameParts.length > 1 ? nameParts[1] : "");
        
        newUser.setIsActive(true);
        newUser.setIsVerified(false);
        
        User savedUser = userRepository.save(newUser);
        
        // Assegna ruolo USER (id=2)
        roleRepository.assignRoleToUser(savedUser.getId(), 2);
        
        return savedUser;
    }

    public User authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email);
        
        if (user == null) {
            return null;
        }
        
        // Verifica password hashata con BCrypt
        if (user.getPassword() != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        
        return null;
    }
}
