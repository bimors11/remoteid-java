package com.example.livestreamserver.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${database.api.url}")
    private String databaseApiUrl;

    @GetMapping("/active_drones")
    public ResponseEntity<?> getActiveDrones() {
        try {
            String url = databaseApiUrl + "/api/active_drones";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "Failed to fetch active drones");
                return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return ResponseEntity.ok(response.getBody());
        } catch(Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Error fetching active drones");
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/drone_logs/{droneId}")
    public ResponseEntity<?> getDroneLogs(@PathVariable("droneId") String droneId) {
        try {
            String url = databaseApiUrl + "/api/history/" + droneId;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "Failed to fetch drone logs");
                return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return ResponseEntity.ok(response.getBody());
        } catch(Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Error fetching drone logs");
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchDrones(@RequestParam(value="q", defaultValue="") String query) {
        try {
            String url = databaseApiUrl + "/api/search?q=" + query;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "Failed to search drones");
                return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return ResponseEntity.ok(response.getBody());
        } catch(Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Error searching drones");
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
