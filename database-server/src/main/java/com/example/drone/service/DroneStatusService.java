package com.example.drone.service;

import com.example.drone.model.Drone;
import com.example.drone.model.FlightSession;
import com.example.drone.repository.DroneRepository;
import com.example.drone.repository.FlightSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DroneStatusService {

    @Autowired
    private DroneRepository droneRepo;
    @Autowired
    private FlightSessionRepository flightSessionRepo;

    @Transactional
    @Scheduled(fixedDelay = 10000)
    public void deactivateInactiveDrones() {
        LocalDateTime now = LocalDateTime.now();
        List<Drone> drones = droneRepo.findAll();
        for (Drone drone : drones) {
            if (drone.getLastActive() != null && Duration.between(drone.getLastActive(), now).getSeconds() > 10) {
                System.out.println("[INFO] Menandai drone " + drone.getDroneId() + " sebagai inactive.");
                drone.setStatus("inactive");
                flightSessionRepo.findByDroneAndIsActive(drone, true).ifPresent(fs -> {
                    fs.setEndTime(now);
                    fs.setIsActive(false);
                    flightSessionRepo.save(fs);
                });
                droneRepo.save(drone);
            }
        }
    }
}
