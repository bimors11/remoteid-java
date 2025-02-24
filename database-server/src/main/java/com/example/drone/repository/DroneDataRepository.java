package com.example.drone.repository;

import com.example.drone.model.DroneData;
import com.example.drone.model.FlightSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DroneDataRepository extends JpaRepository<DroneData, Long> {
    Optional<DroneData> findFirstByFlightSessionOrderByTimestampDesc(FlightSession flightSession);
    List<DroneData> findByFlightSession(FlightSession flightSession);
}
