package sbolba.film.film.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;

public class FilmAPI {
    private JDBCConnection connection = new JDBCConnection();
    
    // Check database connection
    public boolean testConnection() {
        try (Connection conn = connection.getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    // Execute custom query
    public List<String> getFilmTitles() throws SQLException {
        List<String> titles = new ArrayList<>();
        try (Connection conn = connection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT title FROM films")) {
            while (rs.next()) {
                titles.add(rs.getString("title"));
            }
        }
        return titles;
    }
}