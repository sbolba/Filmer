package sbolba.film.film.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import sbolba.film.film.model.User;
import java.util.List;

@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<User> UserRowMapper = (rs, rowNum) -> {
        User u = new User(
            rs.getInt("id"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getString("email"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getString("avatar_url"),
            rs.getString("bio"),
            rs.getDate("date_of_birth"),
            rs.getString("phone_number"),
            (Boolean)rs.getObject("is_active"),
            (Boolean)rs.getObject("is_verified"),
            rs.getTimestamp("created_at"),
            rs.getTimestamp("updated_at"),
            rs.getTimestamp("last_login")
        );
        return u;
    };

    public User findByUsername(String username) {
        String sql = """
                    SELECT *
                    FROM users.users
                    where username = ?
                    """;
        List<User> users = jdbcTemplate.query(sql, UserRowMapper, username);

        return users.size() > 0 ? users.get(0) : null;
    }

    public List<User> findByRole(String roleName) {
        String sql = """
                    SELECT u.*
                    FROM users.users u
                    JOIN users.user_roles ur ON u.id = ur.user_id
                    JOIN users.roles r ON ur.role_id = r.id
                    WHERE r.name = ?
                    """;
        return jdbcTemplate.query(sql, UserRowMapper, roleName);
    }

    public User findByEmail(String email) {
        String sql = """
                    SELECT *
                    FROM users.users
                    where email = ?
                """;

        List<User> users = jdbcTemplate.query(sql, UserRowMapper, email);
        return users.isEmpty() ? null : users.get(0);
    }

    public User save(User user) {
        String sql = """
            INSERT INTO users.users (username, email, password, first_name, last_name, avatar_url, is_active, is_verified) VALUES 
            (?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id
        """;
        
        Integer id = jdbcTemplate.queryForObject(sql, Integer.class,
            user.getUsername(),
            user.getEmail(),
            user.getPassword(),
            user.getFirstName(),
            user.getLastName(),
            user.getAvatarUrl(),
            user.getIsActive(),
            user.getIsVerified()
        );
        
        user.setId(id);
        return user;
    }

    public void update(User user) {
    String sql = """
        UPDATE users.users
        SET avatar_url = ?, last_login = ?, updated_at = CURRENT_TIMESTAMP
        WHERE id = ?
    """;

    jdbcTemplate.update(sql,
        user.getAvatarUrl(),
        user.getLastLogin(),
        user.getId()
        );
    }
}