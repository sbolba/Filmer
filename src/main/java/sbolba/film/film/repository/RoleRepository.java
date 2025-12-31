package sbolba.film.film.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import sbolba.film.film.model.Role;

@Repository
public class RoleRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Role> RoleRowMapper = new RowMapper<Role>() {
        @Override
        public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
            Role r = new Role(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description")
            );
            return r;
        }
    };

    public List<Role> findByUserId(int userId) {
        String sql = """
                    SELECT r.*
                    FROM users.roles r
                    JOIN users.user_roles ur ON r.id = ur.role_id
                    WHERE ur.user_id = ?
                    """;
        return jdbcTemplate.query(sql, RoleRowMapper, userId);
    }

    public boolean userHasRole(int userId, String roleName) {
        String sql = """
            SELECT COUNT(*) 
            FROM user_roles ur
            INNER JOIN roles r ON ur.role_id = r.id
            WHERE ur.user_id = ? AND r.name = ?
        """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, roleName);
        return count != null && count > 0;
    }

    public void assignRoleToUser(int userId, int roleId) {
        String sql = """
                INSERT INTO users.user_roles (user_id, role_id)
                VALUES (?, ?)
                """;

        jdbcTemplate.update(sql, userId, roleId);
    }
}
