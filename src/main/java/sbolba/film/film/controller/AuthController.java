package sbolba.film.film.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import sbolba.film.film.model.User;
import sbolba.film.film.service.UserService;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String email = request.get("email");
        String password = request.get("password");

        // Validazione input
        if (email == null || password == null || name == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "All fields are required"));
        }
        
        // Validazione robustezza password
        if (password.length() < 8) {
            return ResponseEntity.badRequest().body(Map.of("error", "Password must be at least 8 characters"));
        }
        
        // Validazione formato email
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid email format"));
        }
        
        // Verifica se l'email esiste già
        User existingUser = userService.findByEmail(email);
        if (existingUser != null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already exists"));
        }

        // Crea nuovo utente - la password viene criptata automaticamente nel service
        User newUser = userService.registerUser(name, email, password);
        
        return ResponseEntity.ok(Map.of(
            "message", "User registered successfully",
            "userId", newUser.getId()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        String email = request.get("email");
        String password = request.get("password");

        // Validazione input
        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email and password are required"));
        }
        
        if (password.isEmpty() || email.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email and password cannot be empty"));
        }

        // La password in chiaro viene confrontata con quella hashata nel service usando BCrypt
        User user = userService.authenticateUser(email, password);
        
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }

        // Create Spring Security authentication
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(user.getEmail(), null, Collections.emptyList());
        
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
        
        // Store authentication in session
        httpRequest.getSession().setAttribute(
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, 
            securityContext
        );

        return ResponseEntity.ok(Map.of(
            "message", "Login successful",
            "userId", user.getId(),
            "email", user.getEmail()
        ));
    }
}
