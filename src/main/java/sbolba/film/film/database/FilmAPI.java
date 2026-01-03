package sbolba.film.film.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

/**
 * Classe per operazioni database personalizzate.
 * Usa JdbcTemplate con HikariCP connection pooling per scalabilità.
 */
@Component
public class FilmAPI {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private DataSource dataSource;
    
    /**
     * Verifica la connessione al database usando il connection pool.
     * Non crea nuove connessioni, riusa quelle del pool.
     */
    public boolean testConnection() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Ottiene tutti i titoli dei film.
     * Usa PreparedStatement automaticamente tramite JdbcTemplate per sicurezza.
     */
    public List<String> getFilmTitles() {
        String sql = "SELECT title FROM films.film";
        return jdbcTemplate.queryForList(sql, String.class);
    }
    
    /**
     * Ottiene il numero di connessioni attive nel pool HikariCP.
     * Utile per monitoring e debugging.
     */
    public String getConnectionPoolStats() {
        try {
            com.zaxxer.hikari.HikariDataSource hikari = (com.zaxxer.hikari.HikariDataSource) dataSource;
            return String.format("Pool: %s | Active: %d | Idle: %d | Total: %d | Waiting: %d",
                hikari.getPoolName(),
                hikari.getHikariPoolMXBean().getActiveConnections(),
                hikari.getHikariPoolMXBean().getIdleConnections(),
                hikari.getHikariPoolMXBean().getTotalConnections(),
                hikari.getHikariPoolMXBean().getThreadsAwaitingConnection()
            );
        } catch (Exception e) {
            return "Unable to get pool stats: " + e.getMessage();
        }
    }
}