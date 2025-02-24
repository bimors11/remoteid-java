package com.example.drone.repository;

import com.example.drone.model.Drone;
import com.example.drone.model.FlightSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FlightSessionRepository extends JpaRepository<FlightSession, Long> {
    Optional<FlightSession> findByDroneAndIsActive(Drone drone, boolean isActive);
    List<FlightSession> findByDrone(Drone drone);
}
