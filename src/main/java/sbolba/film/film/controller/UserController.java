package sbolba.film.film.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import sbolba.film.film.dto.UserDTO;
import sbolba.film.film.model.User;
import sbolba.film.film.model.Role;
import sbolba.film.film.repository.RoleRepository;
import sbolba.film.film.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;
    
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal OAuth2User oAuth2Principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        
        String email = null;
        
        // Check if OAuth2 login
        if (oAuth2Principal != null) {
            email = oAuth2Principal.getAttribute("email");
        } else {
            // Form-based login - email is stored as the principal name
            email = authentication.getName();
        }
        
        if (email == null) {
            return ResponseEntity.status(401).build();
        }
        
        User user = userService.findByEmail(email);
        
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        List<Role> roles = roleRepository.findByUserId(user.getId());

        UserDTO dto = new UserDTO(user, roles);
        
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username);
        
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        
        List<Role> roles = roleRepository.findByUserId(user.getId());
        
        // Crea la DTO
        UserDTO dto = new UserDTO(user, roles);
        
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/by-role/{roleName}")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable String roleName) {
        List<UserDTO> users = userService.getUsersByRole(roleName);
        return ResponseEntity.ok(users);
    }
}