package com.example.drone.controller;

import com.example.drone.model.Drone;
import com.example.drone.model.DroneData;
import com.example.drone.model.FlightSession;
import com.example.drone.repository.DroneDataRepository;
import com.example.drone.repository.DroneRepository;
import com.example.drone.repository.FlightSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class DroneController {

    @Autowired
    private DroneRepository droneRepo;
    @Autowired
    private DroneDataRepository droneDataRepo;
    @Autowired
    private FlightSessionRepository flightSessionRepo;

    // Endpoint: Mendapatkan daftar drone aktif
    @GetMapping("/active_drones")
    public ResponseEntity<List<Map<String, Object>>> getActiveDrones() {
        List<Drone> drones = droneRepo.findByStatus("active");
        List<Map<String, Object>> result = new ArrayList<>();

        for (Drone drone : drones) {
            Optional<FlightSession> activeSessionOpt = flightSessionRepo.findByDroneAndIsActive(drone, true);
            if (activeSessionOpt.isPresent()) {
                FlightSession activeSession = activeSessionOpt.get();
                droneDataRepo.findFirstByFlightSessionOrderByTimestampDesc(activeSession).ifPresent(latestData -> {
                    Map<String, Object> droneInfo = new HashMap<>();
                    droneInfo.put("id", drone.getDroneId());
                    droneInfo.put("pilot", drone.getPilot() != null ? drone.getPilot().getName() : "Unknown");
                    droneInfo.put("latitude", latestData.getLatitude());
                    droneInfo.put("longitude", latestData.getLongitude());
                    droneInfo.put("altitude", latestData.getAltitude());
                    droneInfo.put("speed", latestData.getSpeed());
                    result.add(droneInfo);
                });
            }
        }
        return ResponseEntity.ok(result);
    }

    // Endpoint: Mendapatkan daftar sesi penerbangan berdasarkan Pilot ID
    @GetMapping("/flight_sessions/pilot/{pilotId}")
    public ResponseEntity<List<Map<String, Object>>> getFlightSessionsByPilot(@PathVariable String pilotId) {
        List<FlightSession> sessions = flightSessionRepo.findByDrone_Pilot_Id(pilotId);
        List<Map<String, Object>> result = sessions.stream().map(session -> {
            Map<String, Object> sessionData = new HashMap<>();
            sessionData.put("session_id", session.getId());
            sessionData.put("drone_id", session.getDrone().getDroneId());
            sessionData.put("start_time", session.getStartTime());
            sessionData.put("end_time", session.getEndTime());
            return sessionData;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // Endpoint: Mendapatkan riwayat penerbangan berdasarkan droneId
    @GetMapping("/history/{droneId}")
    public ResponseEntity<?> getFlightHistory(@PathVariable String droneId) {
        Drone drone = droneRepo.findByDroneId(droneId)
                .orElseThrow(() -> new RuntimeException("Drone not found"));

        List<FlightSession> sessions = flightSessionRepo.findByDrone(drone);
        List<Map<String, Object>> history = sessions.stream().map(session -> {
            List<DroneData> dataList = droneDataRepo.findByFlightSession(session);
            List<Map<String, Object>> dataDetails = dataList.stream().map(d -> {
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("latitude", d.getLatitude());
                dataMap.put("longitude", d.getLongitude());
                dataMap.put("altitude", d.getAltitude());
                dataMap.put("barometer_altitude", d.getBarometerAltitude());
                dataMap.put("speed", d.getSpeed());
                dataMap.put("timestamp", d.getTimestamp());
                return dataMap;
            }).collect(Collectors.toList());

            Map<String, Object> sessionData = new HashMap<>();
            sessionData.put("session_id", session.getId());
            sessionData.put("start_time", session.getStartTime());
            sessionData.put("end_time", session.getEndTime());
            sessionData.put("data", dataDetails);
            return sessionData;
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("drone_id", droneId);
        response.put("history", history);
        return ResponseEntity.ok(response);
    }

    // Endpoint: Mendapatkan detail koordinat dari sesi penerbangan tertentu (untuk replay)
    @GetMapping("/flight_sessions/{sessionId}")
    public ResponseEntity<Map<String, Object>> getFlightSessionDetails(@PathVariable Long sessionId) {
        FlightSession session = flightSessionRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Flight session not found"));

        List<DroneData> telemetry = session.getTelemetryData();
        
        List<Map<String, Object>> coordinates = telemetry.stream().map(d -> {
            Map<String, Object> coord = new HashMap<>();
            coord.put("latitude", d.getLatitude());
            coord.put("longitude", d.getLongitude());
            coord.put("altitude", d.getAltitude());
            coord.put("timestamp", d.getTimestamp());
            return coord;
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("session_id", session.getId());
        response.put("drone_id", session.getDrone().getDroneId());
        response.put("start_time", session.getStartTime());
        response.put("end_time", session.getEndTime());
        response.put("coordinates", coordinates);

        return ResponseEntity.ok(response);
    }

    // Endpoint: Pencarian drone berdasarkan droneId atau nama pilot
    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchDrones(@RequestParam(name="q", required=false, defaultValue="") String query) {
        List<Drone> drones = droneRepo.findByDroneIdContainingIgnoreCaseOrPilot_NameContainingIgnoreCase(query, query);
        List<Map<String, Object>> response = drones.stream().map(drone -> {
            Map<String, Object> map = new HashMap<>();
            map.put("drone_id", drone.getDroneId());
            map.put("pilot", drone.getPilot() != null ? drone.getPilot().getName() : "Unknown");
            map.put("status", drone.getStatus());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
