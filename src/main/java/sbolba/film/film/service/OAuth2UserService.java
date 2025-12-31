package sbolba.film.film.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import sbolba.film.film.repository.UserRepository;
import sbolba.film.film.repository.RoleRepository;
import sbolba.film.film.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        
        String provider = userRequest.getClientRegistration().getRegistrationId();
        String email = processOAuthPostLogin(oAuth2User, provider, userRequest);
        
        // Se l'email non era negli attributi originali, crea un nuovo OAuth2User con l'email
        if (email != null && oAuth2User.getAttribute("email") == null) {
            Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
            attributes.put("email", email);
            
            return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                attributes,
                "email"
            );
        }
        
        return oAuth2User;
    }

    private String processOAuthPostLogin(OAuth2User oAuth2User, String provider, OAuth2UserRequest userRequest) {
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        
        // GitHub usa "avatar_url", Google usa "picture"
        String picture = "github".equals(provider) 
            ? oAuth2User.getAttribute("avatar_url")
            : oAuth2User.getAttribute("picture");
        
        // GitHub: se email è null, richiedila tramite API
        if (email == null && "github".equals(provider)) {
            email = fetchGitHubEmail(userRequest);
        }
        
        // Se email è ancora null, blocca il login
        if (email == null) {
            throw new OAuth2AuthenticationException("Email not available from OAuth provider. Please make your email public in GitHub settings.");
        }
        
        // Cerca utente esistente
        User existingUser = userRepository.findByEmail(email);

        if (existingUser == null) {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUsername(null);
            newUser.setPassword(null);
            newUser.setFirstName(getFirstName(name));
            newUser.setLastName(getLastName(name));
            newUser.setAvatarUrl(picture);
            newUser.setIsActive(true);
            newUser.setIsVerified(true);

            try {
                User savedUser = userRepository.save(newUser);
                roleRepository.assignRoleToUser(savedUser.getId(), 2);
                return email;
            } catch (Exception e) {
                throw new OAuth2AuthenticationException("Failed to save user");
            }
        } else {
            existingUser.setAvatarUrl(picture);
            existingUser.setLastLogin(new java.sql.Timestamp(System.currentTimeMillis()));
            try {
                userRepository.update(existingUser);
            } catch (Exception e) {
                // Silent fail on update
            }
            return email;
        }
    }

    private String fetchGitHubEmail(OAuth2UserRequest userRequest) {
        try {
            String emailsUrl = "https://api.github.com/user/emails";
            String accessToken = userRequest.getAccessToken().getTokenValue();
            
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.set("Accept", "application/vnd.github.v3+json");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<List> response = restTemplate.exchange(
                emailsUrl, 
                HttpMethod.GET, 
                entity, 
                List.class
            );
            
            List<Map<String, Object>> emails = response.getBody();
            if (emails != null) {
                // Cerca l'email primaria
                for (Map<String, Object> emailObj : emails) {
                    Boolean primary = (Boolean) emailObj.get("primary");
                    Boolean verified = (Boolean) emailObj.get("verified");
                    if (Boolean.TRUE.equals(primary) && Boolean.TRUE.equals(verified)) {
                        return (String) emailObj.get("email");
                    }
                }
                // Se non c'è primaria, prendi la prima verificata
                for (Map<String, Object> emailObj : emails) {
                    Boolean verified = (Boolean) emailObj.get("verified");
                    if (Boolean.TRUE.equals(verified)) {
                        return (String) emailObj.get("email");
                    }
                }
            }
        } catch (Exception e) {
            // Silent fail
        }
        return null;
    }

    private String getFirstName(String name) {
        if (name == null || name.isBlank()) {
            return "";
        }

        String[] parts = name.split(" ");
        String result = "";
        for (int i = 0; i < parts.length - 1; i++) {
            result += parts[i] + " ";
        }
        return result.trim();
    }

    private String getLastName(String name) {
        if (name == null || name.isBlank() || !name.contains(" ")) {
            return "";
        }

        String[] parts = name.split(" ");
        return parts[parts.length - 1];
    }

}