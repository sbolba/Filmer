package sbolba.film.film.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sbolba.film.film.database.FilmAPI;

import java.util.Map;

/**
 * Controller per health checks e monitoring del sistema.
 * Utile per verificare lo stato del connection pool e della connessione al database.
 */
@RestController
@RequestMapping("/api/health")
@CrossOrigin(origins = "*")
public class HealthController {

    @Autowired
    private FilmAPI filmAPI;

    /**
     * Verifica lo stato generale del sistema.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        boolean dbConnected = filmAPI.testConnection();
        
        return ResponseEntity.ok(Map.of(
            "status", dbConnected ? "UP" : "DOWN",
            "database", dbConnected ? "connected" : "disconnected",
            "timestamp", System.currentTimeMillis()
        ));
    }

    /**
     * Ottiene statistiche dettagliate sul connection pool HikariCP.
     * Utile per debugging e monitoring delle performance.
     */
    @GetMapping("/pool")
    public ResponseEntity<Map<String, Object>> poolStats() {
        String stats = filmAPI.getConnectionPoolStats();
        boolean dbConnected = filmAPI.testConnection();
        
        return ResponseEntity.ok(Map.of(
            "status", dbConnected ? "UP" : "DOWN",
            "poolStats", stats,
            "timestamp", System.currentTimeMillis()
        ));
    }
}
