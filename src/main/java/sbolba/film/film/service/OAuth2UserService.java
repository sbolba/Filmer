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
import org.springframework.core.ParameterizedTypeReference;

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
        //contact the provider with the access token and get the user info
        OAuth2User oAuth2User = super.loadUser(userRequest);
        
        //get the provider and the email
        String provider = userRequest.getClientRegistration().getRegistrationId();
        String email = processOAuthPostLogin(oAuth2User, provider, userRequest);
        
        //if the email was not in the attributes provided by GitHub, create a new OAuth2User with the added email
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
        
        //get google/github specific picture/avatar_url attribute
        String picture = "github".equals(provider) 
            ? oAuth2User.getAttribute("avatar_url")
            : oAuth2User.getAttribute("picture");
        
        //GitHub: try to fetch email via separate API if not present
        if (email == null && "github".equals(provider)) {
            email = fetchGitHubEmail(userRequest);
        }
        
        //if email is still null, block login (email not available)
        if (email == null) {
            throw new OAuth2AuthenticationException("Email not available from OAuth provider. Please make your email public in GitHub settings.");
        }
        
        // find existing user
        User existingUser = userRepository.findByEmail(email);

        //if user not exists, create new user, else update existing user
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
            //emails getter API from GitHub API docs
            String emailsUrl = "https://api.github.com/user/emails";
            //Oauth2 access token
            String accessToken = userRequest.getAccessToken().getTokenValue();
            
            //create a "browser" to call the API
            RestTemplate restTemplate = new RestTemplate();
            //set the metadata information in order to get the right data from the browser
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.set("Accept", "application/vnd.github.v3+json");
            
            //call the API and receive the informations needed (response)
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                emailsUrl, 
                HttpMethod.GET, 
                entity, 
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            
            //get the body (emails) of the response
            List<Map<String, Object>> emails = response.getBody();
            if (emails != null) {
                // get primary verified email
                for (Map<String, Object> emailObj : emails) {
                    Boolean primary = (Boolean) emailObj.get("primary");
                    Boolean verified = (Boolean) emailObj.get("verified");
                    if (Boolean.TRUE.equals(primary) && Boolean.TRUE.equals(verified)) {
                        return (String) emailObj.get("email");
                    }
                }
                // if no primary, get the first verified
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

    //ausiliar methods to split name into first name and last name
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