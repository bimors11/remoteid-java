package com.example.drone.controller;

import com.example.drone.model.Drone;
import com.example.drone.model.DroneData;
import com.example.drone.model.FlightSession;
import com.example.drone.repository.DroneDataRepository;
import com.example.drone.repository.DroneRepository;
import com.example.drone.repository.FlightSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class DroneController {

    @Autowired
    private DroneRepository droneRepo;
    @Autowired
    private DroneDataRepository droneDataRepo;
    @Autowired
    private FlightSessionRepository flightSessionRepo;

    // Endpoint: Mendapatkan drone aktif
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

    // Endpoint: Mendapatkan riwayat penerbangan berdasarkan droneId
    @GetMapping("/history/{droneId}")
    public ResponseEntity<?> getFlightHistory(@PathVariable String droneId) {
        Optional<Drone> droneOpt = droneRepo.findByDroneId(droneId);
        if (!droneOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "Drone not found"));
        }
        Drone drone = droneOpt.get();
        List<FlightSession> sessions = flightSessionRepo.findByDrone(drone);
        List<Map<String, Object>> history = new ArrayList<>();

        for (FlightSession session : sessions) {
            List<DroneData> dataList = droneDataRepo.findByFlightSession(session);
            List<Map<String, Object>> dataDetails = new ArrayList<>();
            for (DroneData d : dataList) {
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("latitude", d.getLatitude());
                dataMap.put("longitude", d.getLongitude());
                dataMap.put("altitude", d.getAltitude());
                dataMap.put("barometer_altitude", d.getBarometerAltitude());
                dataMap.put("speed", d.getSpeed());
                dataMap.put("timestamp", d.getTimestamp());
                dataDetails.add(dataMap);
            }
            Map<String, Object> sessionData = new HashMap<>();
            sessionData.put("start_time", session.getStartTime());
            sessionData.put("end_time", session.getEndTime());
            sessionData.put("data", dataDetails);
            history.add(sessionData);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("drone_id", droneId);
        response.put("history", history);
        return ResponseEntity.ok(response);
    }

    // Endpoint: Pencarian drone berdasarkan droneId atau nama pilot
    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchDrones(@RequestParam(name="q", required=false, defaultValue="") String query) {
        List<Drone> drones = droneRepo.findByDroneIdContainingIgnoreCaseOrPilot_NameContainingIgnoreCase(query, query);
        List<Map<String, Object>> response = new ArrayList<>();
        for (Drone drone : drones) {
            Map<String, Object> map = new HashMap<>();
            map.put("drone_id", drone.getDroneId());
            map.put("pilot", drone.getPilot() != null ? drone.getPilot().getName() : "Unknown");
            map.put("status", drone.getStatus());
            response.add(map);
        }
        return ResponseEntity.ok(response);
    }
}
