package com.example.drone.repository;

import com.example.drone.model.Drone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DroneRepository extends JpaRepository<Drone, Long> {
    Optional<Drone> findByDroneId(String droneId);
    List<Drone> findByStatus(String status);
    List<Drone> findByDroneIdContainingIgnoreCaseOrPilot_NameContainingIgnoreCase(String droneId, String name);
}
