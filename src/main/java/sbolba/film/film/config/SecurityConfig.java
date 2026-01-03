package sbolba.film.film.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import sbolba.film.film.service.OAuth2UserService;

@Configuration
public class SecurityConfig {

    @Autowired
    private OAuth2UserService oAuth2UserService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/home.html", "/login.html", "/css/**", "/js/**", "/api/films/**", "/api/auth/**", "/api/users/**", "/api/health/**").permitAll()
                .requestMatchers("/edit.html").authenticated()  //only users with role DIRECTOR can access edit.html
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login.html")
                .defaultSuccessUrl("/home.html", true) //if login is successful, redirect to home.html
                .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
                .failureUrl("/login.html?error=true") //if login fails, redirect to login.html with error param
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login.html") //logout always redirects to login page
                .permitAll()
            )
            .anonymous(anonymous -> anonymous.disable()); // Disabilita anonymous authentication
        return http.build();
    }
}